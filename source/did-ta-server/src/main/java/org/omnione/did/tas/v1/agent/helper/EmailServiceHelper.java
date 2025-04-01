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
import org.omnione.did.base.datamodel.enums.PayloadType;
import org.omnione.did.base.datamodel.enums.QrType;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.base.util.QrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.common.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for EmailService.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceHelper {

    /**
     * Generates email data for issuer VC.
     *
     * @param object Object to generate email data from.
     * @param entity Entity to generate email data from.
     * @return Generated email data.
     * @throws IOException if an error occurs while serializing the object.
     */
    public Map<String, String> generateEmailDataForIssuerVc(Object object, Entity entity) throws IOException {
        OfferData offerData = generateOfferData(PayloadType.ISSUE_VC, object);
        String json = JsonUtil.serializeToJson(offerData);

        Map<String, String> emailData = new HashMap<>();

        IssueOfferPayload issueOfferPayload = (IssueOfferPayload) object;
        emailData.put("issuerName", entity.getName());
        emailData.put("qrImg", generateQrData(json));
        emailData.put("qrExpiredDate", DateTimeUtil.convertUtcToFormattedString(issueOfferPayload.getValidUntil()));

        return emailData;
    }

    /**
     * Generates an OfferData object.
     *
     * @param payloadType Type of push notification.
     * @param object Object to generate email data from.
     * @return Generated OfferData.
     */
    private OfferData generateOfferData(PayloadType payloadType, Object object) throws IOException {
        String json = JsonUtil.serializeToJson(object);

        return OfferData.builder()
                .payloadType(payloadType.toString())
                .payload(BaseMultibaseUtil.encode(json.getBytes(StandardCharsets.UTF_8)))
                .build();
    }

    /**
     * Generates email data for restoring DID.
     *
     * @param object Object to generate email data from.
     * @param user User to generate email data from.
     * @return Generated email data.
     * @throws IOException if an error occurs while serializing the object.
     */
    public Map<String, String> generateEmailDataForRestoreDid(Object object, User user) throws IOException {
        OfferData offerData = generateOfferData(PayloadType.RESTORE_DID, object);
        String json = JsonUtil.serializeToJson(offerData);

        Map<String, String> emailData = new HashMap<>();

        emailData.put("did", user.getDid());
        emailData.put("qrImg", generateQrData(json));

        return emailData;
    }

    /**
     * Generates QR data.
     *
     *
     * @param json JSON data to generate QR data from.
     * @return Generated QR data.
     */
    private String generateQrData(String json) {
        return QrUtil.convertQrImage(json);
    }

    /**
     * Gets the email title for the given QR type.
     *
     * @param qrType QR type to get the email title for.
     * @return Email title for the given QR type.
     * @throws OpenDidException if the QR type is invalid.
     */
    public String getEmailTitle(QrType qrType) {
        switch (qrType) {
            case ISSUE_VC:
                return "Certificate Issuance Request";
            case RESTORE_DID:
                return "DID Revocation Request";
            default:
                throw new OpenDidException(ErrorCode.INVALID_QR_TYPE);
        }
    }
}
