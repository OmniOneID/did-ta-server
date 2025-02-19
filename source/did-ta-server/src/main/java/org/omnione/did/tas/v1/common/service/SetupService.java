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

package org.omnione.did.tas.v1.common.service;

import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.db.repository.TasRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.TasProperty;
import org.omnione.did.base.util.BaseBlockChainUtil;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.tas.v1.agent.dto.common.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.setup.RemoveBlockChainIndexReqDto;
import org.omnione.did.tas.v1.agent.service.SignatureService;
import org.omnione.did.tas.v1.agent.service.StorageService;
import org.omnione.did.tas.v1.common.service.query.EntityQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * Service for setting up the TAS and entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SetupService {
    private final StorageService storageService;
    private final SignatureService signatureService;
    private final TasQueryService tasQueryService;
    private final TasProperty tasProperty;
    private final TasRepository tasRepository;
    private final EntityQueryService entityQueryService;
    private final EntityRepository entityRepository;

    /**
     * Registers the TAS DID document.
     *
     * @param didDoc The DID document to be registered.
     * @return Empty response.
     * @throws OpenDidException if the registration fails.
     */
    public EmptyResDto registerTasDidDocument(MultipartFile didDoc) {
        try {
            log.debug("=== Starting registerTasDidDocument ===");

            // Parse the DID Document
            log.debug("\t--> Parsing DID Document");
            DidManager didManager = BaseCoreDidUtil.parseDidDoc(new String(didDoc.getBytes(), StandardCharsets.UTF_8));
            DidDocument ownerDidDoc = didManager.getDocument();

            // Verify TAS DID
            log.debug("\t--> Verifying TAS DID");
            verifyTasDid(ownerDidDoc.getId());

            // Check if TAS is not registered.
            log.debug("\t--> Verifying TAS registration");
            verifyTasNotRegistered(ownerDidDoc.getId());

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID document key signatures");
            signatureService.verifyDidDocKeyProofs(ownerDidDoc);

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            InvokedDidDoc invokedDidDoc = signatureService.signTasInvokedDidDoc(ownerDidDoc);

            // Upload User DID document.
            log.debug("\t--> Uploading wallet DID document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.TAS);

            // Register TAS DID document.
            log.debug("\t--> Registering TAS DID document");
            tasRepository.save(Tas.builder()
                    .did(ownerDidDoc.getId())
                    .name(tasProperty.getName())
                    .serverUrl(tasProperty.getUrl())
                    .status(TasStatus.CERTIFICATE_VC_REQUIRED)
                    .build());

            log.debug("=== Finished registerTasDidDocument ===");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("Failed to register DID Document : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register DID Document : {}", e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    public EmptyResDto registerTasDidDocument(byte[] didDocBytes) {
        try {
            log.debug("=== Starting registerTasDidDocument ===");

            // Parse the DID Document
            log.debug("\t--> Parsing DID Document");
            DidManager didManager = BaseCoreDidUtil.parseDidDoc(new String(didDocBytes, StandardCharsets.UTF_8));
            DidDocument ownerDidDoc = didManager.getDocument();

            // Verify TAS DID
            log.debug("\t--> Verifying TAS DID");
            verifyTasDid(ownerDidDoc.getId());

            // Check if TAS is not registered.
            log.debug("\t--> Verifying TAS registration");
            verifyTasNotRegistered(ownerDidDoc.getId());

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID document key signatures");
            signatureService.verifyDidDocKeyProofs(ownerDidDoc);

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            InvokedDidDoc invokedDidDoc = signatureService.signTasInvokedDidDoc(ownerDidDoc);

            // Upload User DID document.
            log.debug("\t--> Uploading wallet DID document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.TAS);

            // Register TAS DID document.
            log.debug("\t--> Registering TAS DID document");
            tasRepository.save(Tas.builder()
                    .did(ownerDidDoc.getId())
                    .name(tasProperty.getName())
                    .serverUrl(tasProperty.getUrl())
                    .status(TasStatus.CERTIFICATE_VC_REQUIRED)
                    .build());

            log.debug("=== Finished registerTasDidDocument ===");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("Failed to register DID Document : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register DID Document : {}", e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Updates the TAS DID document.
     *
     * @param requestTasdid The DID of the request.
     * @throws OpenDidException if Failed to process TAS DID Document: invalid document
     */
    private void verifyTasDid(String requestTasdid) {
        String tasDid = tasProperty.getDid();
        if (!requestTasdid.equals(tasDid)) {
            log.error("The DID document is not a TAS DID document.");
            throw new OpenDidException(ErrorCode.INVALID_TAS_DIDDOC);
        }
    }

    /**
     * Checks if the TAS is already registered.
     *
     * @param did The DID of the TAS.
     * @throws OpenDidException if the TAS is already registered.
     */
    private void verifyTasNotRegistered(String did) {
        long count = tasQueryService.countByDid(did);
        if (count > 0) {
            log.error("TAS is already registered.");
            throw new OpenDidException(ErrorCode.TAS_ALREADY_REGISTERED);
        }
    }

    /**
     * Updates the TAS DID document.
     *
     * @param didDoc The DID document to be updated.
     * @return Empty response.
     * @throws OpenDidException if the update fails.
     */
    public EmptyResDto updateTasDidDocument(MultipartFile didDoc) {
        try {
            log.debug("=== Starting updateTasDidDocument ===");

            // Parse the DID Document
            log.debug("\t--> Parsing DID Document");
            DidManager didManager = BaseCoreDidUtil.parseDidDoc(new String(didDoc.getBytes(), StandardCharsets.UTF_8));
            DidDocument ownerDidDoc = didManager.getDocument();

            // Verify TAS DID
            log.debug("\t--> Verifying TAS DID");
            verifyTasDid(ownerDidDoc.getId());

            // Check if TAS is already registered.
            log.debug("\t--> Verifying TAS registration");
            verifyTasRegistered();

            // Retrieve the previous DID Document.
            log.debug("\t--> Retrieving previous DID Document");
            DidDocument previousDidDoc = storageService.findDidDoc(ownerDidDoc.getId());

            // Verify the version of the DID Document.
            log.debug("\t--> Verifying the version of the DID Document");
            verifyDidDocVersionIsNewer(previousDidDoc, ownerDidDoc);

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID document key signatures");
            signatureService.verifyDidDocKeyProofs(ownerDidDoc);

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            InvokedDidDoc invokedDidDoc = signatureService.signTasInvokedDidDoc(ownerDidDoc);

            // Upload TAS DID document.
            log.debug("\t--> Uploading wallet DID document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.TAS);

            log.debug("=== Finished updateTasDidDocument ===");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("Failed to update DID Document : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to update DID Document : {}", e.getMessage());
            throw new OpenDidException(ErrorCode.UPDATE_DID_DOC_FAILED);
        }
    }

    /**
     * Checks if the TAS is registered.
     *
     * @throws OpenDidException if the TAS is not registered.
     */
    private void verifyTasRegistered() {
        long count = tasQueryService.countByDid(tasProperty.getDid());
        if (count == 0) {
            log.error("TAS is not registered.");
            throw new OpenDidException(ErrorCode.TAS_NOT_REGISTERED);
        }
    }

    /**
     * Verifies the version of the DID Document.
     *
     * @param oldDidDoc The previous DID Document.
     * @param newDidDoc The new DID Document.
     * @throws OpenDidException if the version of the DID Document is not newer than the previous version.
     */
    private void verifyDidDocVersionIsNewer(DidDocument oldDidDoc, DidDocument newDidDoc) {
        if (Integer.parseInt(oldDidDoc.getVersionId()) + 1 != Integer.parseInt(newDidDoc.getVersionId())) {
            log.error("The version of the DID document is not newer than the previous version.");
            throw new OpenDidException(ErrorCode.INVALID_DIDDOC_VERSION);
        }
    }

    /**
     * Registers the Entity DID document.
     *
     * @param didDoc The DID document to be registered.
     * @param roleType The role type of the entity.
     * @param serverUrl The server URL of the entity.
     * @param name The name of the entity.
     * @return Empty response.
     * @throws OpenDidException if the registration fails.
     */
    public EmptyResDto registerEntityDidDocument(MultipartFile didDoc, String roleType, String serverUrl, String certificateUrl, String name) {
        try {
            log.debug("=== Starting registerEntityDidDocument ===");

            // Parse the DID Document
            log.debug("\t--> Parsing DID Document");
            DidManager didManager = BaseCoreDidUtil.parseDidDoc(new String(didDoc.getBytes(), StandardCharsets.UTF_8));
            DidDocument ownerDidDoc = didManager.getDocument();

            // Check if Entity is already registered.
            log.debug("\t--> Verifying TAS registration");
            verifyTasRegistered();

            // Check if Entity is already registered.
            log.debug("\t--> Verifying Entity registration");
            verifyEntityRegistered(ownerDidDoc.getId());

            // Verify DID document key signatures.
            log.debug("\t--> Verifying DID document key signatures");
            signatureService.verifyDidDocKeyProofs(ownerDidDoc);

            // Sign DID document.
            log.debug("\t--> Signing DID document");
            InvokedDidDoc invokedDidDoc = signatureService.signEntityInvokedDidDoc(ownerDidDoc);
            log.debug(invokedDidDoc.toJson());

            // Upload Entity DID document.
            log.debug("\t--> Uploading DID document");
            storageService.registerDidDoc(invokedDidDoc, RoleType.fromString(roleType));

            // Insert Entity information.
            log.debug("\t--> Inserting Entity information");
            entityRepository.save(Entity.builder()
                    .did(ownerDidDoc.getId())
                    .name(name)
                    .role(Role.fromRoleType(RoleType.fromString(roleType)))
                    .serverUrl(serverUrl)
                    .certificateUrl(certificateUrl)
                    .status(EntityStatus.CERTIFICATE_VC_REQUIRED)
                    .build());

            log.debug("=== Finished registerEntityDidDocument ===");

        } catch (OpenDidException e) {
            log.error("Failed to register DID Document : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register DID Document : {}", e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }

        return new EmptyResDto();
    }

    /**
     * verify if the entity is already registered.
     *
     * @param did The DID of the entity.
     * @throws OpenDidException if the update fails.
     */
    private void verifyEntityRegistered(String did) {
        long count = entityQueryService.countByDid(did);
        if (count > 0) {
            log.error("Entity is already registered.");
            throw new OpenDidException(ErrorCode.ENTITY_ALREADY_REGISTERED);
        }
    }

    /**
     * remove blockchain index
     * @param removeBlockChainIndexReqDto The request DTO for removing blockchain index
     * @return Empty response.
     * @throws OpenDidException if the removal fails.
     */
    public EmptyResDto removeBlockchainIndex(RemoveBlockChainIndexReqDto removeBlockChainIndexReqDto) {
        try {
            log.debug("=== Starting removeBlockchainIndex ===");
            BaseBlockChainUtil.removeIndex(removeBlockChainIndexReqDto.getIndex());
            log.debug("=== Finished removeBlockchainIndex ===");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("Failed to remove blockchain index : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to remove blockchain index : {}", e.getMessage());
            throw new RuntimeException("Failed to remove blockchain index");
        }
    }

    public EmptyResDto removeBlockchainAll() {
        try {
            log.debug("=== Starting removeBlockchainAll ===");
            BaseBlockChainUtil.removeIndexAll();
            log.debug("=== Finished removeBlockchainAll ===");

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("Failed to remove blockchain : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to remove blockchain : {}", e.getMessage());
            throw new RuntimeException("Failed to remove blockchain");
        }
    }
}
