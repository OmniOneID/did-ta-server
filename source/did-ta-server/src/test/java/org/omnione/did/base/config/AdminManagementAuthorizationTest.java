package org.omnione.did.base.config;

import org.junit.jupiter.api.Test;
import org.omnione.did.tas.v1.admin.controller.AdminManagementController;
import org.omnione.did.tas.v1.admin.service.AdminManagementService;
import org.omnione.did.tas.v1.admin.service.JwtService;
import org.omnione.did.tas.v1.common.service.ApiLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.function.Supplier;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminManagementController.class)
@Import(SecurityConfig.class)
class AdminManagementAuthorizationTest {
    private static final String ADMINS = "/tas/admin/v1/admins";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminManagementService adminManagementService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ApiLogService apiLogService;

    private void assertRootOnly(Supplier<MockHttpServletRequestBuilder> request) throws Exception {
        mockMvc.perform(request.get()).andExpect(status().isUnauthorized());
        mockMvc.perform(request.get().with(user("normal").roles("NORMAL")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(adminManagementService);
        mockMvc.perform(request.get().with(user("root").roles("ROOT")))
                .andExpect(status().isOk());
    }

    @Test
    void onlyRootCanRegisterAdmins() throws Exception {
        assertRootOnly(() -> post(ADMINS).contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"new@example.com\",\"loginPassword\":\"hash\",\"role\":\"NORMAL\"}"));
    }

    @Test
    void onlyRootCanResetAnotherAdminsPassword() throws Exception {
        assertRootOnly(() -> post(ADMINS + "/root/reset-password").contentType(MediaType.APPLICATION_JSON)
                .content("{\"loginId\":\"other@example.com\",\"newPassword\":\"hash\"}"));
    }

    @Test
    void onlyRootCanDeleteAdmins() throws Exception {
        assertRootOnly(() -> delete(ADMINS).param("id", "7"));
    }
}
