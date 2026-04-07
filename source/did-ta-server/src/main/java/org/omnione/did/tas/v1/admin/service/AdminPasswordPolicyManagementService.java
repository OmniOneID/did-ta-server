package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.repository.AdminPasswordPolicyRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.tas.v1.admin.dto.admin.AdminPasswordPolicyDto;
import org.omnione.did.tas.v1.admin.dto.admin.RegisterAdminPasswordPolicyReqDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminPasswordPolicyManagementService {
    private final AdminPasswordPolicyRepository adminPasswordPolicyRepository;

    public AdminPasswordPolicyDto findAdminPasswordPolicy() {
        AdminPasswordPolicy policy = adminPasswordPolicyRepository.findTop1ByOrderByIdAsc()
                .orElseThrow(() -> new OpenDidException(ErrorCode.ADMIN_INFO_NOT_FOUND));
        return AdminPasswordPolicyDto.fromAdminPasswordPolicy(policy);
    }

    public AdminPasswordPolicyDto registerAdminPasswordPolicy(RegisterAdminPasswordPolicyReqDto req) {
        AdminPasswordPolicy policy = adminPasswordPolicyRepository.findTop1ByOrderByIdAsc().orElse(null);
        if (policy == null) {
            policy = AdminPasswordPolicy.builder()
                    .minLength(req.getMinLength())
                    .requireUppercase(req.getRequireUppercase())
                    .requireNumber(req.getRequireNumber())
                    .requireSpecial(req.getRequireSpecial())
                    .passwordExpiryDays(req.getPasswordExpiryDays())
                    .build();
        } else {
            policy.setMinLength(req.getMinLength());
            policy.setRequireUppercase(req.getRequireUppercase());
            policy.setRequireNumber(req.getRequireNumber());
            policy.setRequireSpecial(req.getRequireSpecial());
            policy.setPasswordExpiryDays(req.getPasswordExpiryDays());
        }
        return AdminPasswordPolicyDto.fromAdminPasswordPolicy(adminPasswordPolicyRepository.save(policy));
    }
}
