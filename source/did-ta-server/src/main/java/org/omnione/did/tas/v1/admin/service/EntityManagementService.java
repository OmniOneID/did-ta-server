/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.v1.admin.dto.entity.SendEntityInfoReqDto;
import org.omnione.did.tas.v1.agent.service.FileWalletService;
import org.omnione.did.tas.v1.agent.service.IssueVcService;
import org.omnione.did.tas.v1.admin.dto.entity.EntityInfoDto;
import org.omnione.did.tas.v1.admin.dto.entity.SendCertificateVcReqDto;
import org.omnione.did.tas.v1.admin.dto.entity.VerifyEntityNameUniqueResDto;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EntityManagementService {
    private final EntityQueryService entityQueryService;
    private final DidDocService didDocService;
    private final SetupProperty setupProperty;
    private final SetupService setupService;
    private final TasQueryService tasQueryService;
    private final IssueVcService issueVcService;
    private final StorageService storageService;
    private final FileWalletService fileWalletService;
    private final EntityRepository entityRepository;

    public Page<EntityInfoDto> searchEntities(String searchKey, String searchValue, Pageable pageable) {
        return entityQueryService.searchEntities(searchKey, searchValue, pageable);
    }

    public EntityInfoDto findEntity(Long id) {
        Entity entity = entityQueryService.findEntityById(id);
        log.debug("\t--> Found Entity: {}", entity);

        log.debug("\t--> Finding Entity DID Document");
        if (entity.getStatus() == EntityStatus.COMPLETED || entity.getStatus() == EntityStatus.CERTIFICATE_VC_REQUIRED) {
            DidDocument entityDidDocument = didDocService.getDidDocumentOrNull(entity.getDid());
            return EntityInfoDto.fromEntity(entity, entityDidDocument);
        }

        return EntityInfoDto.fromEntity(entity);
    }

    public VerifyEntityNameUniqueResDto verifyNameIsUnique(String name) {
        long count = entityQueryService.countByName(name);
        return VerifyEntityNameUniqueResDto.builder()
                .isUnique(count == 0)
                .build();
    }

    /**
     * This method is temporarily used before the completion of Admin Phase 2 development.
     */
    public EmptyResDto registerEntitiesSimple() {

        log.debug("=== Starting registerEntitiesSimple ===");

        // Retrieve TAS
        log.debug("\t--> Retrieving TAS");
        Tas existedTas = tasQueryService.findTas();

        log.debug("\t--> Registering Issuer");
        registerEntitiesSimple("issuer", "Issuer",RoleType.ISSUER, "8091", existedTas);

        log.debug("\t--> Registering Verifier");
        registerEntitiesSimple("verifier", "Verifier", RoleType.VERIFIER, "8092", existedTas);

        log.debug("\t--> Registering CAS");
        registerEntitiesSimple("cas", "CAS", RoleType.APP_PROVIDER, "8094", existedTas);

        log.debug("\t--> Registering Wallet");
        registerEntitiesSimple("wallet", "WalletService",RoleType.WALLET_PROVIDER, "8095", existedTas);

        log.debug("*** Finished registerEntitiesSimple ***");

        return EmptyResDto.builder().build();
    }

    private void registerEntitiesSimple(String entityName, String directoryPath, RoleType roleType, String port, Tas tas) {
        try {
            String did = "did:omn:" + entityName;
            Entity entity = entityQueryService.findEntityByDidOrNull(did);
            BaseUrls baseUrls = constructBaseUrls(entityName, directoryPath, port);

            if (entity == null) {
                registerNewEntity(did, roleType, baseUrls, entityName, tas);
            } else {
                registerOrUpdateEntity(entity, tas, baseUrls);
            }

            sendEntityInfoToEntity(baseUrls.entityInfoUrl, SendEntityInfoReqDto.builder()
                    .did(did)
                    .name(entityName)
                    .serverUrl(baseUrls.baseUrl)
                    .certificateUrl(baseUrls.certificateUrl)
                    .build());
        } catch (Exception e) {
            log.error("\t--> Failed to register entity: {}", entityName, e);
        }
    }

    private void registerNewEntity(String did, RoleType roleType, BaseUrls baseUrls, String entityName, Tas tas) throws Exception {
        File didDocFile = new File(baseUrls.didDocFilePath);
        byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());

        registerEntityDidDocument_simple(didDocBytes, roleType, baseUrls.baseUrl, baseUrls.certificateUrl, entityName);

        Entity updatedEntity = entityQueryService.findEntityByDid(did);
        VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(updatedEntity, tas);
        sendCertificateVcToEntity(baseUrls.sendCertificateUrl, verifiableCredential);
    }

    private void registerOrUpdateEntity(Entity entity, Tas tas, BaseUrls baseUrls) {
        if (entity.getStatus() == EntityStatus.CERTIFICATE_VC_REQUIRED || entity.getStatus() == EntityStatus.COMPLETED) {
            VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(entity, tas);
            sendCertificateVcToEntity(baseUrls.sendCertificateUrl, verifiableCredential);
        }
    }

    private BaseUrls constructBaseUrls(String entityName, String directoryPath, String port) {
        String baseUrl = setupProperty.getBaseUrl() + ":" + port + "/" + entityName;
        return new BaseUrls(
                baseUrl,
                baseUrl + "/api/v1/certificate-vc",
                baseUrl + "/admin/v1/certificate-vc",
                baseUrl + "/admin/v1/entity-info",
                setupProperty.getPath() + "/" + directoryPath + "/" + entityName + ".did"
        );
    }

    private static class BaseUrls {
        private final String baseUrl;
        private final String certificateUrl;
        private final String sendCertificateUrl;
        private final String entityInfoUrl;
        private final String didDocFilePath;

        public BaseUrls(String baseUrl, String certificateUrl, String sendCertificateUrl, String entityInfoUrl, String didDocFilePath) {
            this.baseUrl = baseUrl;
            this.certificateUrl = certificateUrl;
            this.sendCertificateUrl = sendCertificateUrl;
            this.entityInfoUrl = entityInfoUrl;
            this.didDocFilePath = didDocFilePath;
        }
    }

    public void registerEntityDidDocument_simple(byte[] didDocBytes, RoleType roleType, String url, String certificateVcUrl, String name) {
        setupService.registerEntityDidDocument(didDocBytes, roleType.getRawValue(), url, certificateVcUrl, name);
    }

    public VerifiableCredential issueEntityCertificateVc_simple(Entity entity, Tas tas) {
        VerifiableCredential entityCertificateVc = generateEntityCertificateVc(entity, tas);
        signTasCertificateVc(entityCertificateVc, tas);
        registerEntityCertificateVcMeta(entityCertificateVc, entity);
        updateEntityStatus(entity.getId(), EntityStatus.COMPLETED);

        return entityCertificateVc;
    }

    private VerifiableCredential generateEntityCertificateVc(Entity entity, Tas tas) {
        IssueVcParam issueVcParam = new IssueVcParam();

        issueVcService.setCertificateVcSchema(issueVcParam);
        issueVcService.setIssuer(issueVcParam, tas, tas.getCertificateUrl());
        issueVcService.setEntityClaimInfo(issueVcParam, entity);
        issueVcService.setCertificateVcTypes(issueVcParam);
        issueVcService.setCertificateEvidence(issueVcParam, tas);
        issueVcService.setValidateUntil(issueVcParam,1);

        return issueVcService.generateEntityCertificateVc(issueVcParam, entity);
    }

    private void signTasCertificateVc(VerifiableCredential entityCertificateVc, Tas tas) {
        DidDocument tasDidDoc = storageService.findDidDoc(tas.getDid());
        List<SignatureVcParams> SignatureParamslist = extractVcSignatureMessage(tasDidDoc, entityCertificateVc);

        for(SignatureVcParams signatureParam : SignatureParamslist) {
            String originData = signatureParam.getOriginData();
            log.debug("originData: {}", originData);
            byte[] signatureBytes = fileWalletService.generateCompactSignature(signatureParam.getKeyId(), originData);
            String encodedSignature = BaseMultibaseUtil.encode(signatureBytes);
            signatureParam.setSignatureValue(encodedSignature);
        }

        BaseCoreVcUtil.setVcProof(entityCertificateVc, SignatureParamslist);
    }
    private List<SignatureVcParams> extractVcSignatureMessage(DidDocument tasDidDoc, VerifiableCredential verifiableCredential) {
        return BaseCoreVcUtil.extractVcSignatureMessage(tasDidDoc, verifiableCredential);
    }

    private void registerEntityCertificateVcMeta(VerifiableCredential verifiableCredential, Entity entity) {
        VcMeta vcMeta = BaseCoreVcUtil.generateVcMeta(verifiableCredential, entity.getCertificateUrl());
        storageService.registerVcMeta(vcMeta);
    }

    private void updateEntityStatus(Long id, EntityStatus entityStatus) {
        Entity entity = entityQueryService.findEntityById(id);
        entity.setStatus(entityStatus);

        entityRepository.save(entity);
    }

    private void sendCertificateVcToEntity(String url, VerifiableCredential entityCertificateVc) {
        try {
            String encodedEntityCertificateVc = BaseMultibaseUtil.encode(entityCertificateVc.toJson().getBytes());

            SendCertificateVcReqDto sendCertificateVcReqDto = SendCertificateVcReqDto.builder()
                    .certificateVc(encodedEntityCertificateVc)
                    .build();

            String request = JsonUtil.serializeToJson(sendCertificateVcReqDto);
            HttpClientUtil.postData(url, request, EmptyResDto.class);
        } catch (Exception e) {
            log.error("\t--> Failed to send certificate vc to entity: {}", url, e);
        }
    }

    /**
     * This method is temporarily used before the completion of Admin Phase 2 development.
     * TA sends the entity information to each entity server.
     */
    private void sendEntityInfoToEntity(String url, SendEntityInfoReqDto sendEntityInfoReqDto) {
        try {
            String request = JsonUtil.serializeToJson(sendEntityInfoReqDto);
            HttpClientUtil.postData(url, request, EmptyResDto.class);
        } catch (Exception e) {
            log.error("\t--> Failed to send entity info to entity: {}", url, e);
        }

    }
}
