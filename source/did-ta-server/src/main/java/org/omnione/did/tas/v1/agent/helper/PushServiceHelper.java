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

package org.omnione.did.tas.v1.agent.helper;

import org.omnione.did.base.datamodel.data.IssueOfferPayload;
import org.omnione.did.base.datamodel.data.OfferData;
import org.omnione.did.base.datamodel.data.RestoreDidOfferPayload;
import org.omnione.did.base.datamodel.enums.PayloadType;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.noti.v1.agent.dto.push.FcmNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for PushService.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PushServiceHelper {

    /**
     * Generates a notification for VC or VP issuance.
     *
     * @param payloadType Type of push notification.
     * @param entity Entity to generate notification from.
     * @return Generated notification.
     */
    //@TODO: This method should be refactored to retrieve the push title and body from the properties file
    public FcmNotificationDto generateVcOrVpNotification(PayloadType payloadType, Entity entity) {
        return switch (payloadType) {
            case ISSUE_VC -> FcmNotificationDto.builder()
                    .title("Open DID")
                    .body(entity.getName() + " has requested certificate issuance. Please complete the certificate issuance after verification.")
                    .build();
            case SUBMIT_VP -> FcmNotificationDto.builder()
                    .title("Open DID")
                    .body(entity.getName() + " has requested certificate submission. Please complete the certificate submission after verification.")
                    .build();
            default -> throw new OpenDidException(ErrorCode.INVALID_PUSH_TYPE);
        };
    }

    /**
     * Generates a notification for DID restoration.
     *
     * @param payloadType Type of push notification.
     * @param user User to generate notification from.
     * @return Generated notification.
     */
    //@TODO: This method should be refactored to retrieve the push title and body from the properties file
    public FcmNotificationDto generateDidNotification(PayloadType payloadType, User user) {
        if (Objects.requireNonNull(payloadType) == PayloadType.RESTORE_DID) {
            return FcmNotificationDto.builder()
                    .title("Open DID")
                    .body(user.getDid() + " has requested DID revocation. Please complete the DID revocation after verification.")
                    .build();
        }
        throw new OpenDidException(ErrorCode.INVALID_PUSH_TYPE);
    }

    /**
     * generates a notification for VC issuance.
     *
     * @param issueOfferPayload Payload to generate notification from.
     * @param entity Entity to generate notification from.
     * @return Generated notification data to be sent.
     * @throws OpenDidException if an error occurs while generating the notification.
     */
    //@TODO: This method should be refactored to retrieve the push title and body from the properties file
    public Map<String, String> generatePushDataForIssueVc(IssueOfferPayload issueOfferPayload, Entity entity) {
        try {
            String json = JsonUtil.serializeToJson(issueOfferPayload);

            OfferData offerData = generateOfferData(PayloadType.ISSUE_VC, json);
            String offerDataJson = JsonUtil.serializeAndSort(offerData);

            Map<String, String> pushData = new HashMap<>();
            pushData.put("offerData", offerDataJson);
            pushData.put("title", "Open DID");
            pushData.put("body", entity.getName() + " has requested a certificate issuance. Please complete the certificate issuance after verification.");

            return pushData;
        } catch (CommonSdkException e) {
            log.error("Failed to generate push data for vc issuance", e);
            throw new OpenDidException(ErrorCode.PUSH_DATA_GENERATION_FAILED);
        }
    }

    /**
     * Generates a notification for DID restoration.
     *
     * @param restoreDidOfferPayload Payload to generate notification from.
     * @param did DID to generate notification from.
     * @return Generated notification data to be sent.
     * @throws OpenDidException if an error occurs while generating the notification.
     */
    //@TODO: This method should be refactored to retrieve the push title and body from the properties file
    public Map<String, String> generatePushDataForRestoreDid(RestoreDidOfferPayload restoreDidOfferPayload, String did) {
        try {
            String json = JsonUtil.serializeToJson(restoreDidOfferPayload);

            OfferData offerData = generateOfferData(PayloadType.RESTORE_DID, json);
            String offerDataJson = JsonUtil.serializeAndSort(offerData);

            Map<String, String> pushData = new HashMap<>();
            pushData.put("offerData", offerDataJson);
            pushData.put("title", "Open DID");
            pushData.put("body", did + " has requested DID revocation. Please complete the DID revocation after verification.");

            return pushData;
        } catch (CommonSdkException e) {
            log.error("Failed to generate push data for DID restoration", e);
            throw new OpenDidException(ErrorCode.PUSH_DATA_GENERATION_FAILED);
        }
    }

    /**
     * Generates an OfferData object.
     *
     * @param payloadType Type of push notification.
     * @param json JSON data to generate OfferData from.
     * @return Generated OfferData.
     */
    private OfferData generateOfferData(PayloadType payloadType, String json) {
        return OfferData.builder()
                .payloadType(payloadType.toString())
                .payload(BaseMultibaseUtil.encode(json.getBytes(StandardCharsets.UTF_8)))
                .build();
    }
}
