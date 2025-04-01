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

package org.omnione.did.base.util;

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.core.data.rest.ClaimInfo;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.manager.VcManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.data.model.provider.ProviderDetail;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.DocumentVerificationEvidence;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VerifiableCredential;

import java.util.HashMap;
import java.util.List;

/**
 * Utility class for core VC operations.
 * This class provides methods for parsing VC schema, setting VC properties, generating VCs, and verifying VCs.
 */
@Slf4j
public class BaseCoreVcUtil {

    /**
     * Parses a VC schema from its JSON representation.
     *
     * @param vcSchemaJson The JSON string representing the VC schema.
     * @return The parsed VC schema object.
     */
    public static VcSchema parseVcSchema(String vcSchemaJson) {
        VcSchema vcSchema = new VcSchema();
        vcSchema.fromJson(vcSchemaJson);

        return vcSchema;
    }

    /**
     * Sets the issuer information in the VC issuance parameters.
     *
     * @param issueVcParam The VC issuance parameters.
     * @param issuerDid The DID of the issuer.
     * @param issuerName The name of the issuer.
     * @param certVcRef The reference of the certificate VC.
     */
    public static void setIssuer(IssueVcParam issueVcParam, String issuerDid, String issuerName, String certVcRef) {
        ProviderDetail providerDetail = new ProviderDetail();
        providerDetail.setDid(issuerDid);
        providerDetail.setName(issuerName);
        providerDetail.setCertVcRef(certVcRef);

        issueVcParam.setProviderDetail(providerDetail);
    }

    /**
     * Sets the VC schema in the VC issuance parameters.
     *
     * @param issueVcParam The VC issuance parameters.
     * @param vcSchemaJson The JSON string representing the VC schema.
     */
    public static void setVcSchema(IssueVcParam issueVcParam, String vcSchemaJson) {
        VcSchema vcSchema = parseVcSchema(vcSchemaJson);
        issueVcParam.setVcSchema(vcSchema);
    }

    /**
     * Sets the claim information in the VC issuance parameters.
     *
     * @param issueVcParam The VC issuance parameters.
     * @param claimInfoMap The map containing the claim information.
     */
    public static void setClaimInfo(IssueVcParam issueVcParam, HashMap<String, ClaimInfo> claimInfoMap) {
        issueVcParam.setPrivacy(claimInfoMap);
    }

    /**
     * Sets the VC types in the VC issuance parameters.
     *
     * @param issueVcParam The VC issuance parameters.
     * @param vcTypeList The list of VC types.
     */
    public static void setVcTypes(IssueVcParam issueVcParam, List<VcType> vcTypeList) {
        issueVcParam.setVcType(vcTypeList);
    }

    /**
     * Sets the evidence in the VC issuance parameters.
     *
     * @param issueVcParam The VC issuance parameters.
     * @param evidence The document verification evidence.
     */
    public static void setEvidence(IssueVcParam issueVcParam, DocumentVerificationEvidence evidence) {
        issueVcParam.setEvidences(List.of(evidence));
    }

    /**
     * Generates a Verifiable Credential (VC).
     *
     * @param issueVcParam The VC issuance parameters.
     * @param did The DID of the recipient (VC issuance target).
     * @return The generated Verifiable Credential.
     * @throws OpenDidException if the VC generation fails.
     */
    public static VerifiableCredential generateVc(IssueVcParam issueVcParam, String did) {
        try {
            VcManager vcManager = new VcManager();
            return vcManager.issueCredential(issueVcParam, did);
        } catch (CoreException e) {
            log.error("Failed to generate VC: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.VC_GENERATION_FAILED);
        }
    }

    /**
     * Generates VC metadata.
     *
     * @param vc The VC to generate the metadata for.
     * @param certVcRef The URL of the certificate VC.
     * @return The generated VC metadata.
     */
    public static VcMeta generateVcMeta(VerifiableCredential vc, String certVcRef) {
        VcManager vcManager = new VcManager();
        return vcManager.generateVcMetaData(vc, certVcRef);
    }

    /**
     * Extracts the VC signature message.
     *
     * @param tasDidDoc The DID document of the TAS.
     * @param verifiableCredential The VC to extract the signature message from.
     * @return The extracted VC signature message.
     * @throws OpenDidException if the VC signature message extraction fails.
     */
    public static List<SignatureVcParams> extractVcSignatureMessage(DidDocument tasDidDoc, VerifiableCredential verifiableCredential) {
        try {
            VcManager vcManager = new VcManager();
            return vcManager.getOriginDataForSign("assert", tasDidDoc, verifiableCredential);
        } catch (CoreException e) {
            log.error("Failed to extract VC Signature Message: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.VC_ORIGIN_DATA_EXTRACTION_FAILED);
        }
    }

    /**
     * Sets the proof in a Verifiable Credential (VC).
     *
     * @param verifiableCredential The VC to set the proof for.
     * @param signatureParamsList The list of signature parameters.
     * @throws OpenDidException if the VC proof setting fails.
     */
    public static void setVcProof(VerifiableCredential verifiableCredential, List<SignatureVcParams> signatureParamsList) {
        try {
            VcManager vcManager = new VcManager();
            vcManager.addProof(verifiableCredential, signatureParamsList);
        } catch (CoreException e) {
            log.error("Failed to set VC Proof: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.VC_PROOF_SETTING_FAILED);
        }

    }

    /**
     * Parses VC metadata from its JSON representation.
     *
     * @param vcMetaJson The JSON string representing the VC metadata.
     * @return The parsed VC metadata object.
     */
    public static VcMeta parseVcMeta(String vcMetaJson) {
        VcMeta vcMeta = new VcMeta();
        vcMeta.fromJson(vcMetaJson);

        return vcMeta;
    }

    /**
     * Verifies a Verifiable Credential.
     *
     * @param verifiableCredential The VC to verify.
     * @param didDocument The DID document of the issuer.
     * @param isRevocationCheck Flag indicating whether to perform a revocation check.
     * @throws OpenDidException if the VC verification fails.
     */
    public static void verifyVc(VerifiableCredential verifiableCredential, DidDocument didDocument, boolean isRevocationCheck) {
        VcManager vcManager = new VcManager();
        try {
            vcManager.verifyCredential(verifiableCredential, didDocument, isRevocationCheck);
        } catch (CoreException e) {
            log.error("Failed to verify VC: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.VC_VERIFICATION_FAILED);
        }
    }
}
