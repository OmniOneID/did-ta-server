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

package org.omnione.did.tas.v1.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TasStatus;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.constant.TransactionType;
import org.omnione.did.base.db.domain.CertificateVc;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.repository.CertificateVcRepository;
import org.omnione.did.base.db.repository.TasRepository;
import org.omnione.did.list.v1.service.ListCertificateVcPublishService;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.TaAuthProperty;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasReqDto;
import org.omnione.did.tas.v1.agent.dto.tas.RequestEnrollTasResDto;
import org.omnione.did.tas.v1.agent.service.FileWalletService;
import org.omnione.did.tas.v1.agent.service.IssueVcService;
import org.omnione.did.tas.v1.agent.service.TransactionService;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.IdGenerator;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * TAS service implementation for managing TAS registration and enrollment
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!sample")
public class TasServiceImpl implements TasService {
    private final TasRepository tasRepository;
    private final TasQueryService tasQueryService;
    private final TransactionService transactionService;
    private final StorageService storageService;
    private final CertificateVcRepository certificateVcRepository;
    private final IssueVcService issueVcService;
    private final FileWalletService fileWalletService;
    private final TaAuthProperty taAuthProperty;
    private final JsonParseService jsonParseService;
    private final ListCertificateVcPublishService listCertificateVcPublishService;

    /**
     * Handles the request to enroll a TAS (Trust Anchor Service).
     * This method performs various validations, generates and signs a TAS certificate,
     * updates TAS status, and creates associated transactions.
     *
     * @param requestEnrollTasReqDto The DTO containing the request details for enrolling TAS
     * @return RequestEnrollTasResDto The response DTO containing the certificate VC reference and transaction ID
     * @throws OpenDidException if there's an error during the enrollment process
     */
    @Override
    public RequestEnrollTasResDto requestEnrollTas(RequestEnrollTasReqDto requestEnrollTasReqDto) {
        try {
            log.debug("=== Starting requestEnrollTas ===");

            // Retrieve TAS password.
            log.debug("\t--> Retrieving TAS password.");
            String tasPassword = findTasPassword();

            // Compare passwords.
            log.debug("\t--> Comparing passwords.");
            verifyTasPassword(requestEnrollTasReqDto.getRequest().getPassword(), tasPassword);

            // Find TAS.
            Tas existedTas = tasQueryService.findTas();

            // Verify TAS status.
            log.debug("\t--> Validating TAS status.");
            verifyCertificateVcIssuance(existedTas);

            // Generate TAS certificate VC.
            log.debug("\t--> Generating TAS certificate VC.");
            VerifiableCredential tasCertificateVc = generateTasCertificateVc(existedTas);

            log.debug("\t--> Signing TAS certificate VC.");
            signTasCertificateVc(tasCertificateVc, existedTas);

            // Register TAS certificate VC meta.
            log.debug("\t--> Registering TAS certificate VC meta.");
            registerTasCertificateVcMeta(tasCertificateVc, existedTas);

            // Publish TAS certificate VC.
            log.debug("\t--> Publishing TAS certificate VC.");
            publishTasCertificateVc(tasCertificateVc);

            // Generate transaction code.
            log.debug("\t--> Generating transaction code.");
            String txId = IdGenerator.generateTxId();

            // Update the status of TAS.
            log.debug("\t--> Updating TAS status. (status: COMPLETED)");
            updateTasStatus(TasStatus.COMPLETED);

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information.");
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.TAS_REGISTRATION)
                    .status(TransactionStatus.COMPLETED)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information.");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.REQUEST_ENROLL_TAS)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            return RequestEnrollTasResDto.builder()
                    .certVcRef(existedTas.getCertificateUrl())
                    .txId(txId)
                    .build();
        } catch (OpenDidException e) {
            log.error("Failed to enroll TAS: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to enroll TAS: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.FAILED_API_PROPOSE_ENROLL_TAS);
        }
    }

    /**
     * Retrieves the TAS password for registration.
     *
     * @return String The TAS password
     */
    private String findTasPassword() {
        String password = taAuthProperty.getAuth().getRegistrationPassword();
        if (password == null || password.isEmpty()) {
            throw new OpenDidException(ErrorCode.TAS_PASSWORD_NOT_FOUND);
        }

        return taAuthProperty.getAuth().getRegistrationPassword();
    }

    /**
     * Verifies the TAS password against the stored password.
     *
     * @param requestPassword The password provided in the request
     * @param storedPassword The password stored in the system
     * @throws OpenDidException if the passwords do not match
     */
    private void verifyTasPassword(String requestPassword, String storedPassword) {
        if (!storedPassword.equals(requestPassword)) {
            throw new OpenDidException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    /**
     * Verifies the TAS information based on the expected status.
     *
     * @throws OpenDidException if the TAS is already registered
     */
    private void verifyCertificateVcIssuance(Tas tas) {
        if (tas.getStatus() == TasStatus.COMPLETED) {
            throw new OpenDidException(ErrorCode.TAS_ALREADY_REGISTERED);
        }
    }

    /**
     * Generates a TAS certificate Verifiable Credential (VC).
     *
     * @return VerifiableCredential The generated TAS certificate VC
     */
    private VerifiableCredential generateTasCertificateVc(Tas tas) {
        IssueVcParam issueVcParam = new IssueVcParam();
        issueVcService.setCertificateVcSchema(issueVcParam);
        issueVcService.setIssuer(issueVcParam, tas, tas.getCertificateUrl());
        issueVcService.setTasClaimInfo(issueVcParam, tas);
        issueVcService.setCertificateVcTypes(issueVcParam);
        issueVcService.setCertificateEvidence(issueVcParam, tas);
        issueVcService.setValidateUntil(issueVcParam,1);

        return issueVcService.generateTasCertificateVc(issueVcParam, tas);
    }

    /**
     * Generates a TAS certificate Verifiable Credential (VC).
     *
     * @return VerifiableCredential The generated TAS certificate VC
     */
    private VerifiableCredential generateTasCertificateVc(Tas tas, String dn) {
        IssueVcParam issueVcParam = new IssueVcParam();
        issueVcService.setCertificateVcSchema(issueVcParam);
        issueVcService.setIssuer(issueVcParam, tas, tas.getCertificateUrl());
        issueVcService.setTasClaimInfo(issueVcParam, tas, dn);
        issueVcService.setCertificateVcTypes(issueVcParam);
        issueVcService.setCertificateEvidence(issueVcParam, tas);
        issueVcService.setValidateUntil(issueVcParam,1);

        return issueVcService.generateTasCertificateVc(issueVcParam, tas);
    }

    /**
     * Signs the TAS certificate Verifiable Credential.
     *
     * @param tasCertificateVc The TAS certificate VC to be signed
     */
    private void signTasCertificateVc(VerifiableCredential tasCertificateVc, Tas tas) {
        DidDocument tasDidDoc = storageService.findDidDoc(tas.getDid());
        List<SignatureVcParams> SignatureParamslist = extractVcSignatureMessage(tasDidDoc, tasCertificateVc);

        for(SignatureVcParams signatureParam : SignatureParamslist) {
            String originData = signatureParam.getOriginData();
            log.debug("originData: {}", originData);
            byte[] signatureBytes = fileWalletService.generateCompactSignature(signatureParam.getKeyId(), originData);
            String encodedSignature = BaseMultibaseUtil.encode(signatureBytes);
            signatureParam.setSignatureValue(encodedSignature);
        }

        BaseCoreVcUtil.setVcProof(tasCertificateVc, SignatureParamslist);
    }

    /**
     * Extracts the VC signature message from the TAS DID document and Verifiable Credential.
     *
     * @param tasDidDoc The TAS DID document
     * @param verifiableCredential The Verifiable Credential
     * @return List<SignatureVcParams> The list of signature parameters
     */
    private List<SignatureVcParams> extractVcSignatureMessage(DidDocument tasDidDoc, VerifiableCredential verifiableCredential) {
        return BaseCoreVcUtil.extractVcSignatureMessage(tasDidDoc, verifiableCredential);
    }

    /**
     * Registers the TAS certificate VC metadata.
     *
     * @param verifiableCredential The Verifiable Credential
     * @param tas The TAS object
     */
    private void registerTasCertificateVcMeta(VerifiableCredential verifiableCredential, Tas tas) {
        VcMeta vcMeta = BaseCoreVcUtil.generateVcMeta(verifiableCredential, tas.getCertificateUrl());
        log.debug("tas.getCertificateUrl(): {}", tas.getCertificateUrl());
        log.debug("vcMeta: {}", vcMeta.toJson());
        storageService.registerVcMeta(vcMeta);
    }

    /**
     * Publishes the TAS certificate Verifiable Credential.
     *
     * @param tasCertificateVc The TAS certificate VC to be published
     * @return String The URL of the published certificate VC
     */
    private void publishTasCertificateVc(VerifiableCredential tasCertificateVc) {
        certificateVcRepository.save(CertificateVc.builder()
                .vc(tasCertificateVc.toJson())
                .build());
        try {
            listCertificateVcPublishService.registerCertificateVc(tasCertificateVc);
        } catch (Exception e) {
            log.warn("[NON-CRITICAL] Failed to publish TAS certificate VC to list: {}", e.getMessage());
        }
    }

    /**
     * Updates the TAS certificate VC URL.
     *
     * @param certificateVcUrl The new certificate VC URL
     */
    private void updateTasCertificateVcUrl(String certificateVcUrl) {
        Tas tas = tasQueryService.findTas();
        tas.setCertificateUrl(certificateVcUrl);

        tasRepository.save(tas);
    }

    /**
     * Updates the TAS status.
     *
     * @param tasStatus The new TAS status
     */
    private void updateTasStatus(TasStatus tasStatus) {
        Tas tas = tasQueryService.findTas();
        tas.setStatus(tasStatus);

        tasRepository.save(tas);
    }

    @Override
    public Map<String, Object> generateCertificate(String dn) {
        try {
            log.debug("=== Starting generateCertificate ===");

            // Find TAS.
            Tas existedTas = tasQueryService.findTas();

            // Verify TAS status.
            log.debug("\t--> Validating TAS status.");
            verifyCertificateVcIssuance(existedTas);

            // Generate TAS certificate VC.
            log.debug("\t--> Generating TAS certificate VC.");
            VerifiableCredential tasCertificateVc = generateTasCertificateVc(existedTas, dn);

            log.debug("\t--> Signing TAS certificate VC.");
            signTasCertificateVc(tasCertificateVc, existedTas);

            log.debug("=== Finished generateCertificate ===");

            return jsonParseService.parseCertificateVcToMap(tasCertificateVc.toJson());
        } catch (OpenDidException e) {
            log.error("Failed to enroll TAS: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to enroll TAS: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.FAILED_API_PROPOSE_ENROLL_TAS);
        }
    }

    public RequestEnrollTasResDto requestEnrollTas(String certificate, RequestEnrollTasReqDto requestEnrollTasReqDto) {
        try {
            log.debug("=== Starting requestEnrollTas ===");

            // Retrieve TAS password.
            log.debug("\t--> Retrieving TAS password.");
            String tasPassword = findTasPassword();

            // Compare passwords.
            log.debug("\t--> Comparing passwords.");
            verifyTasPassword(requestEnrollTasReqDto.getRequest().getPassword(), tasPassword);

            // Find TAS.
            Tas existedTas = tasQueryService.findTas();

            // Verify TAS status.
            log.debug("\t--> Validating TAS status.");
            verifyCertificateVcIssuance(existedTas);

            // Parse the certificate.
            log.debug("\t--> Parsing the certificate.");
            VerifiableCredential tasCertificateVc = new VerifiableCredential();
            tasCertificateVc.fromJson(certificate);

            // Register TAS certificate VC meta.
            log.debug("\t--> Registering TAS certificate VC meta.");
            registerTasCertificateVcMeta(tasCertificateVc, existedTas);

            // Publish TAS certificate VC.
            log.debug("\t--> Publishing TAS certificate VC.");
            publishTasCertificateVc(tasCertificateVc);

            // Generate transaction code.
            log.debug("\t--> Generating transaction code.");
            String txId = IdGenerator.generateTxId();

            // Update the status of TAS.
            log.debug("\t--> Updating TAS status. (status: COMPLETED)");
            updateTasStatus(TasStatus.COMPLETED);

            // Insert transaction information.
            log.debug("\t--> Inserting transaction information.");
            Transaction transaction = transactionService.insertTransaction(Transaction.builder()
                    .txId(txId)
                    .type(TransactionType.TAS_REGISTRATION)
                    .status(TransactionStatus.COMPLETED)
                    .expiredAt(transactionService.retrieveTransactionExpiredTime())
                    .build()
            );

            // Insert sub-transaction information.
            log.debug("\t--> Inserting sub-transaction information.");
            transactionService.insertSubTransaction(SubTransaction.builder()
                    .transactionId(transaction.getId())
                    .step(1)
                    .type(SubTransactionType.REQUEST_ENROLL_TAS)
                    .status(SubTransactionStatus.COMPLETED)
                    .build()
            );

            return RequestEnrollTasResDto.builder()
                    .certVcRef(existedTas.getCertificateUrl())
                    .txId(txId)
                    .build();
        } catch (OpenDidException e) {
            log.error("Failed to enroll TAS: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to enroll TAS: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.FAILED_API_PROPOSE_ENROLL_TAS);
        }
    }
}
