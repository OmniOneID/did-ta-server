package org.omnione.did.tas.v1.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.tas.v1.admin.dto.admin.AdminPasswordPolicyDto;
import org.omnione.did.tas.v1.admin.dto.admin.RegisterAdminPasswordPolicyReqDto;
import org.omnione.did.tas.v1.admin.service.AdminPasswordPolicyManagementService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Tas.ADMIN_V1)
public class AdminPasswordPolicyManagementController {
    private final AdminPasswordPolicyManagementService adminPasswordPolicyManagementService;

    @GetMapping(value = "/admin-password-policy")
    public AdminPasswordPolicyDto getAdminPasswordPolicy() {
        return adminPasswordPolicyManagementService.findAdminPasswordPolicy();
    }

    @PostMapping(value = "/admin-password-policy")
    public AdminPasswordPolicyDto registerAdminPasswordPolicy(@Valid @RequestBody RegisterAdminPasswordPolicyReqDto req) {
        return adminPasswordPolicyManagementService.registerAdminPasswordPolicy(req);
    }
}
