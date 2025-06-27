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

import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.Tas;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreVcUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.core.data.rest.ClaimInfo;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.data.model.vc.DocumentVerificationEvidence;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.v1.common.service.query.VcSchemaQueryService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for issuing and managing Verifiable Credentials (VCs).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IssueVcService {
    private final VcSchemaQueryService vcSchemaQueryService;

    /**
     * Sets the certificate VC schema for the given IssueVcParam.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @throws OpenDidException if the VC schema retrieval fails
     */
    public void setCertificateVcSchema(IssueVcParam issueVcParam) {
        VcSchema vcSchema = vcSchemaQueryService.findByVcType(VcType.CERTIFICATE_VC);
        BaseCoreVcUtil.setVcSchema(issueVcParam, vcSchema.getSchema());
    }

    /**
     * Sets the issuer information for the VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param tas The TAS (Trust Anchor Service) entity
     * @param issuerCertVcRef The reference to the issuer's certificate VC
     */
    public void setIssuer(IssueVcParam issueVcParam, Tas tas, String issuerCertVcRef) {
        BaseCoreVcUtil.setIssuer(issueVcParam, tas.getDid(), tas.getName(), issuerCertVcRef);
    }

    /**
     * Sets the TAS claim information for the VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param tas The TAS entity
     */
    public void setTasClaimInfo(IssueVcParam issueVcParam, Tas tas) {
        HashMap<String, ClaimInfo> claimInfoMap = new HashMap<>();

        //@TODO: subject 수정 필요 (예: o=name,c=KR)
        ClaimInfo subjectClaim = new ClaimInfo();
        String subject = String.format("o=%s", tas.getName());
        subjectClaim.setCode("org.opendid.v1.subject");
        subjectClaim.setValue(subject.getBytes(StandardCharsets.UTF_8));

        ClaimInfo roleClaim = new ClaimInfo();
        roleClaim.setCode("org.opendid.v1.role");
        roleClaim.setValue(RoleType.TAS.getRawValue().getBytes(StandardCharsets.UTF_8));

        claimInfoMap.put(subjectClaim.getCode(), subjectClaim);
        claimInfoMap.put(roleClaim.getCode(), roleClaim);

        BaseCoreVcUtil.setClaimInfo(issueVcParam, claimInfoMap);
    }

    /**
     * Sets the TAS claim information for the VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param tas The TAS entity
     */
    public void setTasClaimInfo(IssueVcParam issueVcParam, Tas tas, String dn) {
        HashMap<String, ClaimInfo> claimInfoMap = new HashMap<>();

        ClaimInfo subjectClaim = new ClaimInfo();
        subjectClaim.setCode("org.opendid.v1.subject");
        subjectClaim.setValue(dn.getBytes(StandardCharsets.UTF_8));

        ClaimInfo roleClaim = new ClaimInfo();
        roleClaim.setCode("org.opendid.v1.role");
        roleClaim.setValue(RoleType.TAS.getRawValue().getBytes(StandardCharsets.UTF_8));

        claimInfoMap.put(subjectClaim.getCode(), subjectClaim);
        claimInfoMap.put(roleClaim.getCode(), roleClaim);

        BaseCoreVcUtil.setClaimInfo(issueVcParam, claimInfoMap);
    }

    /**
     * Sets the entity claim information for the VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param entity The entity for which the VC is being issued
     */
    public void setEntityClaimInfo(IssueVcParam issueVcParam, Entity entity) {
        HashMap<String, ClaimInfo> claimInfoMap = new HashMap<>();

        ClaimInfo subjectClaim = new ClaimInfo();
        String subject = String.format("o=%s", entity.getName());
        subjectClaim.setCode("org.opendid.v1.subject");
        subjectClaim.setValue(subject.getBytes(StandardCharsets.UTF_8));

        ClaimInfo roleClaim = new ClaimInfo();
        roleClaim.setCode("org.opendid.v1.role");
        roleClaim.setValue(entity.getRole().toRoleType().getRawValue().getBytes(StandardCharsets.UTF_8));

        claimInfoMap.put(subjectClaim.getCode(), subjectClaim);
        claimInfoMap.put(roleClaim.getCode(), roleClaim);

        BaseCoreVcUtil.setClaimInfo(issueVcParam, claimInfoMap);
    }

    /**
     * Sets the certificate VC types.
     *
     * @param issueVcParam The parameter object for issuing a VC
     */
    public void setCertificateVcTypes(IssueVcParam issueVcParam) {
        List<VcType> vcTypeList = List.of(VcType.VERIFIABLE_CREDENTIAL, VcType.CERTIFICATE_VC);
        BaseCoreVcUtil.setVcTypes(issueVcParam, vcTypeList);
    }

    /**
     * Sets the certificate evidence for the VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param tas The TAS entity
     *
     * Note: The licenseNumber is currently hardcoded but should be set dynamically in future implementations.
     */
    public void setCertificateEvidence(IssueVcParam issueVcParam, Tas tas) {
        DocumentVerificationEvidence documentVerificationEvidence = new DocumentVerificationEvidence();
        documentVerificationEvidence.setType("DocumentVerification");
        documentVerificationEvidence.setVerifier(tas.getDid());
        documentVerificationEvidence.setEvidenceDocument("BusinessLicense");
        documentVerificationEvidence.setSubjectPresence("Physical");
        documentVerificationEvidence.setDocumentPresence("Physical");

        Map<String, String> attribute = generateEvidenceAttribute(new HashMap<String, String>(), "licenseNumber", "1234567890");
        documentVerificationEvidence.setAttribute(attribute);

        BaseCoreVcUtil.setEvidence(issueVcParam, documentVerificationEvidence);
    }

    /**
     * Sets the validation period for the VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param years The number of years the VC should be valid for
     */
    public void setValidateUntil(IssueVcParam issueVcParam, int years) {
        issueVcParam.setValidFrom(DateTimeUtil.getCurrentUTCTime());
        issueVcParam.setValidUntil(DateTimeUtil.addYearsToCurrentTime(years));
    }

    /**
     * Generates an entity certificate VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param entity The entity for which the VC is being generated
     * @return VerifiableCredential The generated entity certificate VC
     */
    public VerifiableCredential generateEntityCertificateVc(IssueVcParam issueVcParam, Entity entity) {
        return BaseCoreVcUtil.generateVc(issueVcParam, entity.getDid());
    }

    /**
     * Generates a TAS certificate VC.
     *
     * @param issueVcParam The parameter object for issuing a VC
     * @param tas The TAS entity for which the VC is being generated
     * @return VerifiableCredential The generated TAS certificate VC
     */
    public VerifiableCredential generateTasCertificateVc(IssueVcParam issueVcParam, Tas tas) {
        return BaseCoreVcUtil.generateVc(issueVcParam, tas.getDid());
    }

    /**
     * Generates a VC for the given entity.
     *
     * @param attribute The attribute map to which the key-value pair will be added
     * @param key The key for the attribute
     * @param value The value for the attribute
     * @return Map<String, String> The updated attribute map
     */
    public Map<String, String> generateEvidenceAttribute(Map<String, String> attribute, String key, String value) {
        if (attribute == null) {
            attribute = new HashMap<>();
        }
        attribute.put(key, value);

        return attribute;
    }
}