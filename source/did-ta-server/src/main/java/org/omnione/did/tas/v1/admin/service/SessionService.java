package org.omnione.did.tas.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.constant.PasswordResetReason;
import org.omnione.did.base.db.domain.Admin;
import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.omnione.did.base.db.repository.AdminPasswordPolicyRepository;
import org.omnione.did.base.db.repository.AdminRepository;
import org.omnione.did.tas.v1.admin.dto.admin.AdminDto;
import org.omnione.did.tas.v1.admin.dto.admin.RequestAdminLoginReqDto;
import org.omnione.did.tas.v1.common.service.query.AdminQueryService;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionService {
    private final AdminQueryService adminQueryService;
    private final AdminPasswordPolicyRepository adminPasswordPolicyRepository;
    private final AdminRepository adminRepository;

    public AdminDto requestAdminLogin(RequestAdminLoginReqDto requestAdminLoginReqDto) {
        Admin admin = adminQueryService.findByLoginIdAndLoginPassword(
                requestAdminLoginReqDto.getLoginId(),
                requestAdminLoginReqDto.getLoginPassword());

        // Skip expiry check if already requires reset for non-EXPIRED reason
        if (Boolean.TRUE.equals(admin.getRequirePasswordReset())
                && admin.getPasswordResetReason() != null
                && admin.getPasswordResetReason() != PasswordResetReason.EXPIRED) {
            return AdminDto.fromAdmin(admin);
        }

        // Check password expiry
        if (checkAndMarkExpired(admin)) {
            adminRepository.save(admin);
        }

        return AdminDto.fromAdmin(admin);
    }

    private boolean checkAndMarkExpired(Admin admin) {
        if (admin.getLastPasswordChangedAt() == null) return false;
        Optional<AdminPasswordPolicy> policyOpt = adminPasswordPolicyRepository.findTop1ByOrderByIdAsc();
        if (policyOpt.isEmpty()) return false;
        AdminPasswordPolicy policy = policyOpt.get();
        if (policy.getPasswordExpiryDays() <= 0) return false;
        Instant expirationDate = admin.getLastPasswordChangedAt()
                .plus(policy.getPasswordExpiryDays(), ChronoUnit.DAYS);
        if (Instant.now().isAfter(expirationDate)) {
            admin.setRequirePasswordReset(true);
            admin.setPasswordResetReason(PasswordResetReason.EXPIRED);
            return true;
        }
        return false;
    }
}
