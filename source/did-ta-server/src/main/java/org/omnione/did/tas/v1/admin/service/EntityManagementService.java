/*
 * Copyright 2024 OmniOne.
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
import org.omnione.did.base.exception.AdminErrorCode;
import org.omnione.did.base.exception.OpenDidAdminException;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.base.property.TasProperty;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.v1.agent.service.FileWalletService;
import org.omnione.did.tas.v1.agent.service.IssueVcService;
import org.omnione.did.tas.v1.common.dto.admin.entity.EntityInfoDto;
import org.omnione.did.tas.v1.common.dto.admin.entity.SendCertificateVcReqDto;
import org.omnione.did.tas.v1.common.dto.admin.entity.VerifyEntityNameUniqueResDto;
import org.omnione.did.tas.v1.common.dto.agent.common.EmptyResDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
    private final TasProperty tasProperty;
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

    public EmptyResDto registerEntitiesSimple() {

        log.debug("=== Starting registerEntitiesSimple ===");

        log.debug("\t--> Registering Issuer");
        registerEntitiesSimple("issuer", RoleType.ISSUER, "8091");

        log.debug("\t--> Registering Verifier");
        registerEntitiesSimple("verifier", RoleType.VERIFIER, "8092");

        log.debug("\t--> Registering CAS");
        registerEntitiesSimple("cas", RoleType.APP_PROVIDER, "8094");

        log.debug("\t--> Registering Wallet");
        registerEntitiesSimple("wallet", RoleType.WALLET_PROVIDER, "8095");

        log.debug("*** Finished registerEntitiesSimple ***");

        return EmptyResDto.builder().build();
    }

    private void registerEntitiesSimple(String entityName, RoleType roleType, String port) {
        try {
            String did = "did:omn:" + entityName;
            Entity entity = entityQueryService.findEntityByDidOrNull(did);
            String baseUrl = setupProperty.getUrl() + ":" + port + "/" + entityName;
            String certificateUrl = baseUrl + "/api/v1/certificate-vc";
            String sendCertificateUrl = baseUrl + "/admin/v1/certificate-vc";

            if (entity == null) {
                File didDocFile = new File(setupProperty.getPath() + entityName + ".did");
                byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());

                registerEntityDidDocument_simple(didDocBytes, roleType, baseUrl, certificateUrl, entityName);

                Entity updatedEntity = entityQueryService.findEntityByDid(did);
                VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(updatedEntity);

                sendCertificateVcToEntity(sendCertificateUrl, verifiableCredential);
            } else if (entity.getStatus() == EntityStatus.CERTIFICATE_VC_REQUIRED) {
                VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(entity);
                sendCertificateVcToEntity(sendCertificateUrl, verifiableCredential);
            } else if (entity.getStatus() == EntityStatus.COMPLETED) {
                VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(entity);
                sendCertificateVcToEntity(sendCertificateUrl, verifiableCredential);
            }
        } catch (Exception e) {
            log.error("\t--> Failed to register entity: {}", entityName, e);
        }
    }

    public void registerEntityDidDocument_simple(byte[] didDocBytes, RoleType roleType, String url, String certificateVcUrl, String name) {
        setupService.registerEntityDidDocument(didDocBytes, roleType.getRawValue(), url, certificateVcUrl, name);
    }

    public VerifiableCredential issueEntityCertificateVc_simple(Entity entity) {
        VerifiableCredential entityCertificateVc = generateEntityCertificateVc(entity);
        signTasCertificateVc(entityCertificateVc);
        registerEntityCertificateVcMeta(entityCertificateVc, entity);
        updateEntityStatus(entity.getId(), EntityStatus.COMPLETED);

        return entityCertificateVc;
    }

    private VerifiableCredential generateEntityCertificateVc(Entity entity) {
        Tas tas = tasQueryService.findTas();
        IssueVcParam issueVcParam = new IssueVcParam();

        issueVcService.setCertificateVcSchema(issueVcParam);
        issueVcService.setIssuer(issueVcParam, tas, tasProperty.getCertificateVc());
        issueVcService.setEntityClaimInfo(issueVcParam, entity);
        issueVcService.setCertificateVcTypes(issueVcParam);
        issueVcService.setCertificateEvidence(issueVcParam, tas);
        issueVcService.setValidateUntil(issueVcParam,1);

        return issueVcService.generateEntityCertificateVc(issueVcParam, entity);
    }

    private void signTasCertificateVc(VerifiableCredential entityCertificateVc) {
        DidDocument tasDidDoc = storageService.findDidDoc(tasProperty.getDid());
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
}
