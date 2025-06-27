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

package org.omnione.did.tas.v1.agent.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.base.db.constant.UserStatus;
import org.omnione.did.base.db.domain.App;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.repository.AppRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCryptoUtil;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.omnione.did.tas.v1.agent.dto.push.UpdatePushTokenReqDto;
import org.omnione.did.tas.v1.common.service.DidDocService;
import org.omnione.did.tas.v1.common.service.query.UserQueryService;
import org.omnione.did.tas.v1.agent.service.validator.DidAuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.common.util.DidValidator;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.springframework.stereotype.Service;

/**
 * Implementation of the PushService interface for managing push notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PushServiceImpl implements PushService {
    private final UserQueryService userQueryService;
    private final AppQueryService appQueryService;
    private final DidAuthValidator didAuthValidator;
    private final AppRepository appRepository;
    private final DidDocService didDocService;

    /**
     * Updates the push token for a user's application.
     *
     * @param updatePushTokenReqDto The DTO containing the update request details
     * @return UpdatePushTokenResDto The response DTO
     * @throws OpenDidException if there's an error during the update process
     */
    @Override
    public EmptyResDto updatePushToken(UpdatePushTokenReqDto updatePushTokenReqDto) {

        try {
            log.debug("=== Starting updatePushToken ===");

            // Retrieve User information.
            log.debug("\t--> Retrieving User information");
            User user = userQueryService.findByDidAndStatus(updatePushTokenReqDto.getDid(), UserStatus.ACTIVATED);

            // Retrieve App information.
            log.debug("\t--> Retrieving App information");
            App app = appQueryService.findByUserId(user.getId());

            // Validate App ID;
            log.debug("\t--> Validating App id");
            validateAppId(updatePushTokenReqDto, app);

            // Update Push token
            log.debug("\t--> Updating Push token");
            updatePushToken(app.getId(), updatePushTokenReqDto.getPushToken());

            return new EmptyResDto();
        } catch (OpenDidException e) {
            log.error("\t--> Exception occurred in updatePushToken: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("\t--> Exception occurred in updatePushToken: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.FAILED_API_UPDATE_PUSH_TOKEN);
        }
    }

    /**
     * Validates the App ID in the request against the stored App information.
     *
     * @param updatePushTokenReqDto The update request DTO
     * @param app The App entity retrieved from the database
     * @throws OpenDidException if the App IDs don't match
     */
    private void validateAppId(UpdatePushTokenReqDto updatePushTokenReqDto, App app) {
        if (!app.getAppId().equals(updatePushTokenReqDto.getAppId())) {
            log.error("\t--> App ID validation failed: expected {}, but received {}", app.getAppId(), updatePushTokenReqDto.getAppId());
            throw new OpenDidException(ErrorCode.APP_ID_MISMATCH);
        }
    }

    /**
     * Validates the proof in the update request.
     *
     * @param updatePushTokenReqDto The update request DTO
     * @param user The User entity retrieved from the database
     * @throws OpenDidException if the proof is invalid
     */
    private void validateProof(UpdatePushTokenReqDto updatePushTokenReqDto, User user) {
        // Extract and validate didKeyUrl
        String verificationMethod = updatePushTokenReqDto.getProof().getVerificationMethod();
        if (!DidValidator.isValidDidKeyUrl(verificationMethod)) {
            log.error("\t--> Invalid DID Key URL: {}", verificationMethod);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the equivalence of did.
        String didOfKeyUrl = DidUtil.extractDid(verificationMethod);
        if (!user.getDid().equals(didOfKeyUrl)) {
            log.error("\t--> DID mismatch: clientDid={}, didOfKeyUrl={}", user.getDid(), didOfKeyUrl);
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Check the purpose of the proof.
        if (updatePushTokenReqDto.getProof().getProofPurpose() != ProofPurpose.ASSERTION_METHOD) {
            log.error("\t--> Invalid proof purpose: {}", updatePushTokenReqDto.getProof().getProofPurpose());
            throw new OpenDidException(ErrorCode.INVALID_SIGNATURE);
        }

        // Extract the signature message.
        byte[] signatureMessage = extractSignatureMessage(updatePushTokenReqDto);

        // Find Wallet Provider DID Document.
        DidDocument clientDidDocument = didDocService.getDidDocument(verificationMethod);

        // Get the Assertion public key.
        String encodedKeyAgreePublicKey = BaseCoreDidUtil.getPublicKey(clientDidDocument, "assert");

        // Verify the signature.
        verifySignature(encodedKeyAgreePublicKey, updatePushTokenReqDto.getProof().getProofValue(), signatureMessage, updatePushTokenReqDto.getProof().getType());
    }

    /**
     * Extracts the signature message from the update request DTO.
     *
     * @param updatePushTokenReqDto The update request DTO
     * @return byte[] The extracted signature message
     * @throws OpenDidException if there's an error during extraction
     */
    private byte[] extractSignatureMessage(UpdatePushTokenReqDto updatePushTokenReqDto) {
        try {
            // Remove proofValue from Proof fields in the object.
            UpdatePushTokenReqDto signatureMessageObject = removeProofValue(updatePushTokenReqDto);

            // Serialize to JSON and remove whitespaces.
            String jsonString = JsonUtil.serializeAndSort(signatureMessageObject);

            // Hash with SHA-256
            return BaseDigestUtil.generateHash(jsonString);
        } catch(CommonSdkException e) {
            log.error("\t--> Exception occurred in extractSignatureMessage: {}", e.getMessage(), e);
            throw new OpenDidException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    /**
     * Removes the proof value from the update request DTO.
     *
     * @param data The original update request DTO
     * @return UpdatePushTokenReqDto A new DTO with the proof value removed
     */
    private UpdatePushTokenReqDto removeProofValue(UpdatePushTokenReqDto data) {
        UpdatePushTokenReqDto signatureMessageObject = UpdatePushTokenReqDto.builder()
                .did(data.getDid())
                .appId(data.getAppId())
                .pushToken(data.getPushToken())
                .proof(new Proof(
                        data.getProof()
                                .getType(),
                        data.getProof()
                                .getCreated(),
                        data.getProof()
                                .getVerificationMethod(),
                        data.getProof()
                                .getProofPurpose(),
                        null
                ))
                .build();

        return signatureMessageObject;
    }

    /**
     * Verifies the signature using the provided public key, signature, and message.
     *
     * @param encodedPublicKey The encoded public key
     * @param signature The signature to verify
     * @param signatureMassage The original message that was signed
     * @param proofType The type of proof used for the signature
     * @throws OpenDidException if signature verification fails
     */
    //@TODO: 공통함수로 빼야 함
    private void verifySignature(String encodedPublicKey, String signature, byte[] signatureMassage, ProofType proofType) {
        BaseCryptoUtil.verifySignature(encodedPublicKey, signature, signatureMassage, proofType.toEccCurveType());
    }
    /**
     * Updates the push token for an application.
     *
     * @param id The ID of the application
     * @param pushToken The new push token
     */
    private void updatePushToken(Long id, String pushToken) {
        App app = appQueryService.findById(id);
        app.setPushToken(pushToken);

        appRepository.save(app);
    }
}
