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
import org.omnione.did.base.db.domain.EntityDidDocument;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.repository.DidDocumentRepository;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.HttpClientException;
import org.omnione.did.common.util.HttpClientUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.v1.admin.dto.admin.RegisterDidFromEntityReqDto;
import org.omnione.did.tas.v1.admin.dto.entity.ApproveDidReqDto;
import org.omnione.did.tas.v1.admin.dto.entity.SendEntityInfoReqDto;
import org.omnione.did.tas.v1.agent.service.FileWalletService;
import org.omnione.did.tas.v1.agent.service.IssueVcService;
import org.omnione.did.tas.v1.admin.dto.entity.EntityInfoDto;
import org.omnione.did.tas.v1.admin.dto.entity.SendCertificateVcReqDto;
import org.omnione.did.tas.v1.admin.dto.entity.VerifyEntityNameUniqueResDto;
import org.omnione.did.tas.v1.agent.service.SignatureService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.omnione.did.tas.v1.common.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.DidDocumentQueryService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.omnione.did.base.db.repository.DidDocumentRepository;

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
    private final SignatureService signatureService;
    private final DidDocumentRepository didDocumentRepository;
    private final DidDocumentQueryService didDocumentQueryService;

    public Page<EntityInfoDto> searchEntities(String searchKey, String searchValue, Pageable pageable) {
        return entityQueryService.searchEntities(searchKey, searchValue, pageable);
    }

    /**
     * Find entity by ID.
     *
     * @param id the ID of the entity
     * @return the entity information
     */
    public EntityInfoDto findEntity(Long id) {
        Entity entity = entityQueryService.findEntityById(id);
        log.debug("\t--> Found Entity: {}", entity);

        DidDocument entityDidDocument = null;
        EntityStatus status = entity.getStatus();

        if (status == EntityStatus.COMPLETED || status == EntityStatus.CERTIFICATE_VC_REQUIRED) {
            log.debug("\t--> Fetching DID Document from blockchain");
            entityDidDocument = didDocService.getDidDocumentOrNull(entity.getDid());
        } else if (status == EntityStatus.DID_DOCUMENT_REQUIRED) {
            log.debug("\t--> Fetching DID Document from DB");
            EntityDidDocument didDocEntity =
                    didDocumentQueryService.findDidDocumentByEntityId(entity.getId());
            entityDidDocument = new DidDocument();
            entityDidDocument.fromJson(didDocEntity.getDidDocument());
        }

        return EntityInfoDto.fromEntity(entity, entityDidDocument);
    }

    /**
     * Check if the entity name is unique.
     *
     * @param name the name to check
     * @return a response indicating whether the name is unique
     */
    public VerifyEntityNameUniqueResDto verifyNameIsUnique(String name) {
        long count = entityQueryService.countByName(name);
        return VerifyEntityNameUniqueResDto.builder()
                .isUnique(count == 0)
                .build();
    }

    /**
     * Register entities with simple configuration. (phase 1)
     *
     * (This method is temporarily used before the completion of Admin Phase 2 development.)
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
        registerEntitiesSimple("cas", "CA", RoleType.APP_PROVIDER, "8094", existedTas);

        log.debug("\t--> Registering Wallet");
        registerEntitiesSimple("wallet", "Wallet",RoleType.WALLET_PROVIDER, "8095", existedTas);

        log.debug("*** Finished registerEntitiesSimple ***");

        return EmptyResDto.builder().build();
    }

    /**
     * Register entities with simple configuration. (phase 1)
     *
     * @param entityName   the name of the entity
     * @param directoryPath the directory path for the DID document
     * @param roleType     the role type of the entity
     * @param port         the port for the entity server
     * @param tas          the TAS instance
     */
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
        } catch (OpenDidException e) {
            log.error("\t--> Failed to register entity: {}", entityName, e);
            throw e;
        } catch (Exception e) {
            log.error("\t--> Failed to register entity: {}", entityName, e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_QUICK_ENTITY);
        }
    }

    /**
     * Register a new entity with the provided information. (phase 1)
     *
     * @param did         the DID of the entity
     * @param roleType    the role type of the entity
     * @param baseUrls    the base URLs for the entity
     * @param entityName  the name of the entity
     * @param tas         the TAS instance
     */
    private void registerNewEntity(String did, RoleType roleType, BaseUrls baseUrls, String entityName, Tas tas) throws Exception {
        File didDocFile = new File(baseUrls.didDocFilePath);
        byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());

        registerEntityDidDocument_simple(didDocBytes, roleType, baseUrls.baseUrl, baseUrls.certificateUrl, entityName);

        Entity updatedEntity = entityQueryService.findEntityByDid(did);
        VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(updatedEntity, tas);
        sendCertificateVcToEntity(baseUrls.sendCertificateUrl, verifiableCredential);
    }

    /**
     * Register or update the entity with the provided information. (phase 1)
     *
     * @param entity      the entity to register or update
     * @param tas         the TAS instance
     * @param baseUrls    the base URLs for the entity
     */
    private void registerOrUpdateEntity(Entity entity, Tas tas, BaseUrls baseUrls) {
        if (entity.getStatus() == EntityStatus.CERTIFICATE_VC_REQUIRED || entity.getStatus() == EntityStatus.COMPLETED) {
            VerifiableCredential verifiableCredential = issueEntityCertificateVc_simple(entity, tas);
            sendCertificateVcToEntity(baseUrls.sendCertificateUrl, verifiableCredential);
        }
    }

    /**
     * Construct the base URLs for the entity. (phase 1)
     *
     * @param entityName   the name of the entity
     * @param directoryPath the directory path for the DID document
     * @param port         the port for the entity server
     * @return a BaseUrls object containing the constructed URLs
     */
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

    /**
     * A class to hold the base URLs for the entity. (phase 1)
     */
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

    /**
     * Register the entity DID document with the provided information. (phase 1)
     *
     * @param didDocBytes      the bytes of the DID document
     * @param roleType         the role type of the entity
     * @param url              the URL for the entity server
     * @param certificateVcUrl the URL for the certificate VC
     * @param name             the name of the entity
     */
    public void registerEntityDidDocument_simple(byte[] didDocBytes, RoleType roleType, String url, String certificateVcUrl, String name) {
        setupService.registerEntityDidDocument(didDocBytes, roleType.getRawValue(), url, certificateVcUrl, name);
    }

    /**
     * Issue a Verifiable Credential (VC) for the entity certificate. (phase 1)
     *
     * @param entity the entity to issue the VC for
     * @param tas    the TAS instance
     * @return the issued Verifiable Credential
     */
    public VerifiableCredential issueEntityCertificateVc_simple(Entity entity, Tas tas) {
        VerifiableCredential entityCertificateVc = generateEntityCertificateVc(entity, tas);
        signTasCertificateVc(entityCertificateVc, tas);
        registerEntityCertificateVcMeta(entityCertificateVc, entity);
        updateEntityStatus(entity.getId(), EntityStatus.COMPLETED);

        return entityCertificateVc;
    }

    /**
     * Generate a Verifiable Credential (VC) for the entity certificate. (phase 1)
     *
     * @param entity the entity to generate the VC for
     * @param tas    the TAS instance
     * @return the generated Verifiable Credential
     */
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

    /**
     * Sign the entity certificate VC with the TAS. (phase 1)
     *
     * @param entityCertificateVc the entity certificate VC to sign
     * @param tas                 the TAS instance
     */
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

    /**
     * Extract the VC signature message from the TAS DID document. (phase 1)
     *
     * @param tasDidDoc            the TAS DID document
     * @param verifiableCredential the Verifiable Credential to extract the signature message from
     * @return a list of SignatureVcParams containing the extracted signature messages
     */
    private List<SignatureVcParams> extractVcSignatureMessage(DidDocument tasDidDoc, VerifiableCredential verifiableCredential) {
        return BaseCoreVcUtil.extractVcSignatureMessage(tasDidDoc, verifiableCredential);
    }

    /**
     * Register the entity certificate VC metadata. (phase 1)
     *
     * @param verifiableCredential the Verifiable Credential to register
     * @param entity               the entity to register the VC for
     */
    private void registerEntityCertificateVcMeta(VerifiableCredential verifiableCredential, Entity entity) {
        VcMeta vcMeta = BaseCoreVcUtil.generateVcMeta(verifiableCredential, entity.getCertificateUrl());
        storageService.registerVcMeta(vcMeta);
    }

    /**
     * Update the status of the entity. (phase 1)
     *
     * @param id           the ID of the entity
     * @param entityStatus the new status of the entity
     */
    private void updateEntityStatus(Long id, EntityStatus entityStatus) {
        Entity entity = entityQueryService.findEntityById(id);
        entity.setStatus(entityStatus);

        entityRepository.save(entity);
    }

    /**
     * Send the entity certificate VC to the entity server. (phase 1)
     *
     * @param url                     the URL of the entity server
     * @param entityCertificateVc     the entity certificate VC to send
     */
    private void sendCertificateVcToEntity(String url, VerifiableCredential entityCertificateVc) {
        try {
            String encodedEntityCertificateVc = BaseMultibaseUtil.encode(entityCertificateVc.toJson().getBytes());

            SendCertificateVcReqDto sendCertificateVcReqDto = SendCertificateVcReqDto.builder()
                    .certificateVc(encodedEntityCertificateVc)
                    .build();

            String request = JsonUtil.serializeToJson(sendCertificateVcReqDto);
            HttpClientUtil.postData(url, request, EmptyResDto.class);
        } catch (OpenDidException e) {
            log.error("\t--> [NON-CRITICAL] Failed to send VC to entity: {}", url, e);
        } catch (HttpClientException e) {
            log.warn("\t--> [NON-CRITICAL] HTTP error while sending VC to entity [{}]", url, e);
        } catch (Exception e) {
            log.warn("\t--> [NON-CRITICAL] Unexpected error while sending VC to entity: {}", url, e);
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
        } catch (HttpClientException e) {
            log.warn("\t--> [NON-CRITICAL] Failed to send entity info via HTTP: {}", url, e);
        } catch (Exception e) {
            log.warn("\t--> [NON-CRITICAL] Unexpected error while sending entity info to entity: {}", url, e);
        }
    }

    /**
     * Register a DID document from an entity.
     * Stores the registration information (including the DID Document) in the database.
     * Admin approval is required to complete the registration.
     *
     * @param registerDidFromEntityReqDto the request DTO containing the DID document and other information
     * @return an empty response DTO
     */
    public EmptyResDto registerDidFromEntity(RegisterDidFromEntityReqDto registerDidFromEntityReqDto) {
        try {
            log.debug("=== Starting registerDidFromEntity ===");
            DidDocument ownerDidDoc = decodeDidDoc(registerDidFromEntityReqDto.getDidDoc());

            // Verifies the key proofs in a DID document.
            log.debug("\t--> Verifying DID document key signatures");
            signatureService.verifyDidDocKeyProofs(ownerDidDoc);

            // Checks if the DID document is already registered.
            log.debug("\t--> Checking for duplicate DID");
            if (entityQueryService.countByDid(ownerDidDoc.getId()) > 0) {
                log.error("\t--> DID already registered: {}", ownerDidDoc.getId());
                throw new OpenDidException(ErrorCode.DID_ALREADY_REGISTERED);
            }
            Tas existedTas = tasQueryService.findTas();
            if (existedTas.getDid()
                    .equals(ownerDidDoc.getId())) {
                log.error("\t--> DID already registered: {}", ownerDidDoc.getId());
                throw new OpenDidException(ErrorCode.DID_ALREADY_REGISTERED);
            }

            // Checks if the entity name is already registered.
            if (entityQueryService.countByName(registerDidFromEntityReqDto.getName()) > 0) {
                log.error("\t--> Entity name already registered: {}", registerDidFromEntityReqDto.getName());
                throw new OpenDidException(ErrorCode.ENTITY_NAME_ALREADY_REGISTERED);
            }

            Entity savedEntity = entityRepository.save(Entity.builder()
                    .did(ownerDidDoc.getId())
                    .name(registerDidFromEntityReqDto.getName())
                    .role(registerDidFromEntityReqDto.getRole())
                    .serverUrl(registerDidFromEntityReqDto.getServerUrl())
                    .certificateUrl(registerDidFromEntityReqDto.getCertificateUrl())
                    .status(EntityStatus.DID_DOCUMENT_REQUIRED)
                    .build());

            // Registers the DID document.
            log.debug("\t--> Registering DID document");
            didDocumentRepository.save(EntityDidDocument.builder()
                    .didDocument(ownerDidDoc.toJson())
                    .entityId(savedEntity.getId())
                    .build());

            log.debug("*** Finished registerDidFromEntity ***");

            return new EmptyResDto();
        } catch (OpenDidException e) {
           log.error("\t--> Failed to register DID from entity: {}", registerDidFromEntityReqDto.getDidDoc(), e);
           throw e;
        } catch (Exception e) {
            log.error("\t--> Failed to register DID from entity: {}", registerDidFromEntityReqDto.getDidDoc(), e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_DID_FROM_ENTITY);
        }
    }

    private DidDocument decodeDidDoc(String encodedDidDoc) {
        log.debug("\t--> Decoding DID Document");
        byte[] decodedData = BaseMultibaseUtil.decode(encodedDidDoc);
        DidDocument didDocument = new DidDocument();
        didDocument.fromJson(new String(decodedData));
        log.debug("\t--> Decoded DID Document: {}", didDocument);
        return didDocument;
    }

    public EmptyResDto approveEntityDidDocument(ApproveDidReqDto approveDidReqDto) {
        try {
            log.debug("=== Starting approveEntityDidDocument ===");

            // Fetch the entity by ID
            log.debug("\t--> Fetching entity by ID: {}", approveDidReqDto.getEntityId());
            Entity existedEntity = entityQueryService.findEntityById(approveDidReqDto.getEntityId());

            // Fetch the did document by entity ID
            log.debug("\t--> Fetching DID document by entity ID: {}", approveDidReqDto.getEntityId());
            EntityDidDocument entityDidDocument = didDocumentQueryService.findDidDocumentByEntityId(existedEntity.getId());

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            DidDocument ownerDidDoc = new DidDocument();
            ownerDidDoc.fromJson(entityDidDocument.getDidDocument());
            InvokedDidDoc invokedDidDoc = signatureService.signEntityInvokedDidDoc(ownerDidDoc);

            // Upload Entity DID document.
            log.debug("\t--> Uploading DID document");
            storageService.registerDidDoc(invokedDidDoc, existedEntity.getRole()
                    .toRoleType());

            // Update Entity information.
            log.debug("\t--> Updating Entity information");
            existedEntity.setStatus(EntityStatus.CERTIFICATE_VC_REQUIRED);

            entityRepository.save(existedEntity);

            log.debug("=== Finished approveEntityDidDocument ===");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("\t--> Failed to approve DID document: {}", approveDidReqDto.getEntityId(), e);
            throw e;
        } catch (Exception e) {
            log.error("\t--> Failed to approve DID document: {}", approveDidReqDto.getEntityId(), e);
            throw new OpenDidException(ErrorCode.FAILED_TO_APPROVE_ENTITY_DID_DOCUMENT);
        }
    }

}
