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

import com.google.gson.JsonParseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.repository.TasRepository;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.SetupProperty;
import org.omnione.did.base.property.TaAuthProperty;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.core.data.rest.DidKeyInfo;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.tas.v1.admin.dto.tas.*;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto.Request;
import org.omnione.did.tas.v1.agent.helper.CertificateVcSchemaProvider;
import org.omnione.did.tas.v1.agent.service.FileWalletService;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.common.service.*;
import org.omnione.did.tas.v1.common.service.query.CertificateVcQueryService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.v1.common.service.query.VcSchemaQueryService;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * This service provides methods for managing TA.
 *
 * @author : yklee0911
 * @fileName : TaManagementService
 * @since : 2/18/25
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaManagementService {

    private final TasQueryService tasQueryService;
    private final SetupProperty setupProperty;
    private final SetupService setupService;
    private final TasService tasService;
    private final DidDocService didDocService;
    private final VcSchemaRepository vcSchemaRepository;
    private final VcSchemaQueryService vcSchemaQueryService;
    private final TaAuthProperty taAuthProperty;
    private final TasRepository tasRepository;
    private final FileWalletService fileWalletService;
    private final CertificateVcQueryService certificateVcQueryService;
    private final JsonParseService jsonParseService;
    private final StorageService storageService;

    /**
     * Request TA information.
     *
     * @return TA information
     */
    public TasInfoResDto requestTaInfo() {
        Tas existedTas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", existedTas);

        if (existedTas == null || existedTas.getStatus() == TasStatus.DID_DOCUMENT_REQUIRED) {
            return TasInfoResDto.fromEntity(existedTas);
        }

        log.debug("\t--> Finding TAS DID Document");
        DidDocument tasDidDocument = findTasDidDocument(existedTas);
        return TasInfoResDto.fromEntity(existedTas, tasDidDocument);
    }

    /**
     * Register TA with simple process. (1st development version)
     *
     * @return TA information
     */
    public TasInfoResDto registerTaSimple(RequestTasInfoReqDto requestTasInfoReqDto) {
        log.debug("=== Starting registerTaSimple ===");

        log.debug("\t--> Finding TAS");
        Tas tas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", tas);

        if (tas == null) {
            log.debug("\t--> TAS is not registered yet. Proceeding with new registration.");
            registerNewTas(requestTasInfoReqDto);
        } else {
            log.debug("\t--> Tas is already registered.");
            processExistingTasRegistration(tas, requestTasInfoReqDto);
        }

        Tas updatedTas = tasQueryService.findTas();

        return buildTasInfoResponse(updatedTas);
    }

    /**
     * Register TA with simple process. (1st development version)
     */
    private void registerNewTas(RequestTasInfoReqDto requestTasInfoReqDto) {
        log.debug("\t--> Registering TA DID Document");
        registerTaDidDocumentSimple(requestTasInfoReqDto.getServerUrl());

        log.debug("\t--> Registering Certificate VC Schema");
        registerCertificateVcSchema(requestTasInfoReqDto.getServerUrl());

        log.debug("\t--> Registering TA Certificate");
        registerTaCertificate();
        log.debug("*** Finished registerTaSimple ***");
    }

    /**
     * Process existing TA registration. (1st development version)
     *
     * @param tas TA
     */
    private void processExistingTasRegistration(Tas tas, RequestTasInfoReqDto requestTasInfoReqDto) {
        switch (tas.getStatus()) {
            case DID_DOCUMENT_REQUIRED:
                log.debug("\t--> Registering TA DID Document");
                registerTaDidDocumentSimple(requestTasInfoReqDto.getServerUrl());

                log.debug("\t--> Registering Certificate VC Schema");
                registerCertificateVcSchema(requestTasInfoReqDto.getServerUrl());

                log.debug("\t--> Registering TA Certificate");
                registerTaCertificate();
                break;

            case CERTIFICATE_VC_REQUIRED:
                log.debug("\t--> Registering TA Certificate");
                registerTaCertificate();
                break;

            default:
                log.error("TA is already registered");
                throw new OpenDidException(ErrorCode.TA_ALREADY_REGISTERED);
        }
    }

    /**
     * Register TA DID Document.
     */
    private void registerTaDidDocumentSimple(String serverUrl) {
        File didDocFile = new File(setupProperty.getPath() + "/TA/tas.did");
        if (!didDocFile.exists() || !didDocFile.isFile()) {
            log.error("DID Document file not found at path: {}", setupProperty.getPath());
            throw new OpenDidException(ErrorCode.FILE_NOT_FOUND);
        }

        try {
            byte[] didDocBytes = Files.readAllBytes(didDocFile.toPath());
            String certificateUrl = serverUrl + "/api/v1/certificate-vc";
            setupService.registerTasDidDocument(didDocBytes, "tas", serverUrl, certificateUrl);
        } catch (IOException e) {
            log.error("I/O error while reading TA DID Document file", e);
            throw new OpenDidException(ErrorCode.FILE_IO_ERROR);
        } catch (OpenDidException e) {
            log.error("OpenDID error while registering TA DID Document", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering TA DID Document", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_TA_DID_DOCUMENT);
        }
    }

    private void registerCertificateVcSchema(String serverUrl) {
        try {
            if (vcSchemaQueryService.findByVcTypeOrNull(VcType.CERTIFICATE_VC) != null) {
                return;
            }

            // Fetch the Certificate VC Schema JSON from the provider
            log.debug("\t--> Fetching Certificate VC Schema JSON from provider");
            String vcSchemaJson = CertificateVcSchemaProvider.getSchema(serverUrl);

            // Parse the JSON into a VcSchema object
            log.debug("\t--> Parsing Certificate VC Schema JSON");
            org.omnione.did.data.model.schema.VcSchema vcSchema =
                    BaseCoreVcUtil.parseVcSchema(vcSchemaJson);

            // Save the VcSchema to the database
            log.debug("\t--> Saving Certificate VC Schema to database");
            vcSchemaRepository.save(VcSchema.builder()
                    .type(VcType.CERTIFICATE_VC)
                    .schema(vcSchema.getSchema())
                    .schemaId(vcSchema.getId())
                    .version(vcSchema.getMetadata()
                            .getFormatVersion())
                    .schema(vcSchema.toJson())
                    .build());

            // Register the VcSchema to the blockchain
            log.debug("\t--> Registering Certificate VC Schema to blockchain");
            registerVcSchemaToBlockchain(vcSchema);
        } catch (JsonParseException | ClassCastException e) {
            log.error("Failed to parse Certificate VC Schema JSON", e);
            throw new OpenDidException(ErrorCode.PARSE_VC_SCHEMA_FAILED);
        } catch (DataAccessException e) {
            log.error("Database error while saving Certificate VC Schema", e);
            throw new OpenDidException(ErrorCode.DB_ERROR_ON_VC_SCHEMA_SAVE);
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering Certificate VC Schema", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_CERTIFICATE_VC_SCHEMA);
        }
    }

    private void registerVcSchemaToBlockchain(org.omnione.did.data.model.schema.VcSchema vcSchema) {
        try {
            Tas foundTasInfo = tasQueryService.findTas();

            storageService.registerVcSchema(vcSchema, foundTasInfo.getDid());

        } catch (OpenDidException e) {
            log.error("Failed to register VC schema to blockchain: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering VC schema to blockchain: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_SCHEMA_REGISTRATION_FAILED);
        }
    }


    /**
     * Register TA certificate.
     */
    private void registerTaCertificate() {
        try {
            RequestEnrollTasReqDto requestEnrollTasReqDto = RequestEnrollTasReqDto.builder()
                    .id("12345")
                    .request(Request.builder()
                            .password("VoOyEuOyal")
                            .build())
                    .build();

            tasService.requestEnrollTas(requestEnrollTasReqDto);
        } catch (OpenDidException e) {
            log.error("Failed to enroll TA", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while enrolling TA", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_TA_CERTIFICATE);
        }
    }

    /**
     * Find TAS DID Document.
     *
     * @return TAS DID Document
     */
    private DidDocument findTasDidDocument(Tas tas) {
        return didDocService.getDidDocument(tas.getDid());
    }

    public EmptyResDto validateTaSecret(ValidateTaSecretReqDto validateTaSecretReqDto) {
        byte[] hashedTaPassword = BaseDigestUtil.generateHash(taAuthProperty.getAuth().getRegistrationPassword());
        String hexedTaPassword = Hex.encodeHexString(hashedTaPassword);

        if (!hexedTaPassword.equals(validateTaSecretReqDto.getSecret())) {
            throw new OpenDidException(ErrorCode.TA_SECRET_NOT_MATCHED);
        }

        return new EmptyResDto();
    }

    public TasInfoResDto registerTaInfo(RegisterTaInfoReqDto registerTaInfoReqDto) {
        Tas tas = tasQueryService.findTasOrNull();
        log.debug("\t--> Found TAS: {}", tas);
        if (tas == null) {
            log.debug("\t--> TAS is not registered yet. Proceeding with new registration.");
            tas = Tas.builder()
                    .name(registerTaInfoReqDto.getName())
                    .serverUrl(registerTaInfoReqDto.getServerUrl())
                    .status(TasStatus.DID_DOCUMENT_REQUIRED)
                    .build();

            tasRepository.save(tas);
            return buildTasInfoResponse(tas);
        }

        if (tas.getStatus() == TasStatus.COMPLETED) {
            log.error("TAS is already registered");
            throw new OpenDidException(ErrorCode.TA_ALREADY_REGISTERED);
        }

        log.debug("\t--> Updating TAS information");
        tas.setName(registerTaInfoReqDto.getName());
        tas.setServerUrl(registerTaInfoReqDto.getServerUrl());
        tasRepository.save(tas);

        return buildTasInfoResponse(tas);
    }

    private TasInfoResDto buildTasInfoResponse(Tas tas) {
        if (tas.getStatus() == TasStatus.DID_DOCUMENT_REQUIRED) {
            return TasInfoResDto.fromEntity(tas);
        }

        log.debug("\t--> Finding TAS DID Document");
        DidDocument didDocument = findTasDidDocument(tas);
        return TasInfoResDto.fromEntity(tas, didDocument);
    }

    /*
     * Register TA DID Document automatically.
     *
     * This method creates a wallet and generates keys if they do not exist,
     * then generates and registers a DID Document.
     *
     * Note:
     * - If the wallet or keys already exist, it will skip creation silently without throwing an error.
     *
     * @return TA DID Document
     */
    public Map<String, Object> registerTaDidDocumentAuto() {
        Tas existedTas = tasQueryService.findTas();
        log.debug("\t--> Found TAS: {}", existedTas);

        if (existedTas.getStatus() != TasStatus.DID_DOCUMENT_REQUIRED) {
            log.error("TAS DID Document is already registered");
            throw new OpenDidException(ErrorCode.TAS_DID_DOCUMENT_ALREADY_REGISTERED);
        }

        // Step1: Create Wallet and keys
        WalletManagerInterface walletManager = initializeWalletWithKeys();

        // Step2: Create DID Document
        DidDocument didDocument = createDidDocumentAuto(walletManager);

        return jsonParseService.parseDidDocToMap(didDocument.toJson());
    }

    /*
     * Generate TA wallet and keys.
     */
    public WalletManagerInterface initializeWalletWithKeys() {
        return fileWalletService.initializeWalletWithKeys();
    }

    /**
     * Create DID Document automatically.
     *
     * This method creates a DID Document using the provided wallet manager.
     *
     * @param walletManager Wallet manager
     * @return Created DID Document
     */
    public DidDocument createDidDocumentAuto(WalletManagerInterface walletManager) {
        String did = "did:omn:tas";

        Map<String, List<ProofPurpose>> purposes = BaseCoreDidUtil.createDefaultProofPurposes();
        List<DidKeyInfo> keyInfos = BaseCoreDidUtil.getDidKeyInfosFromWallet(walletManager, did, purposes);

        DidManager didManager = new DidManager();
        DidDocument unsignedDoc = BaseCoreDidUtil.createDidDocument(didManager, did, did, keyInfos);

        List<String> signingKeys = BaseCoreDidUtil.getSigningKeyIds(purposes);
        DidDocument signedDoc = BaseCoreDidUtil.signAndAddProof(didManager, walletManager, signingKeys);

        return signedDoc;
    }

    /**
     * Register TA DID Document.
     *
     * @param registerTaDidDocumentReqDto Request DTO
     * @return Empty response
     */
    public EmptyResDto registerTaDidDocument(RegisterTaDidDocumentReqDto registerTaDidDocumentReqDto) {
        Tas existedTas = tasQueryService.findTas();
        String certificateUrl = existedTas.getServerUrl() + "/api/v1/certificate-vc";

        return setupService.registerTasDidDocument(registerTaDidDocumentReqDto.getDidDocument().getBytes(),certificateUrl);
    }

    public Map<String, Object> generateTaCertificate(GenerateTaCertificateReqDto generateTaCertificateReqDto) {

        Tas existedTas = tasQueryService.findTas();

        log.debug("\t--> Registering Certificate VC Schema");
        registerCertificateVcSchema(existedTas.getServerUrl());

        return tasService.generateCertificate(generateTaCertificateReqDto.getDn());
    }

    /**
     * Register Certificate VC Schema.
     *
     */
    public Map<String, Object> requestTaCertificateVC() {
        CertificateVc certificateVc = certificateVcQueryService.findCertificateVc();
        return jsonParseService.parseCertificateVcToMap(certificateVc.getVc());
    }

    public EmptyResDto registerTaCertificate(RegisterTaCertificateReqDto registerTaCertificateReqDto) {

        try {
            String registrationPassword = taAuthProperty.getAuth().getRegistrationPassword();
            RequestEnrollTasReqDto requestEnrollTasReqDto = RequestEnrollTasReqDto.builder()
                    .id("12345")
                    .request(Request.builder()
                            .password(registrationPassword)
                            .build())
                    .build();

            tasService.requestEnrollTas(registerTaCertificateReqDto.getCertificate(), requestEnrollTasReqDto);
        } catch (OpenDidException e) {
            log.error("Failed to register TA Certificate", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while registering TA Certificate", e);
            throw new OpenDidException(ErrorCode.FAILED_TO_REGISTER_TA_CERTIFICATE);
        }

        return new EmptyResDto();
    }
}
