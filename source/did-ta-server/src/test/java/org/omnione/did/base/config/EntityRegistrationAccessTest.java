package org.omnione.did.base.config;

import org.junit.jupiter.api.Test;
import org.omnione.did.tas.v1.admin.constant.EntityRegistrationStatus;
import org.omnione.did.tas.v1.admin.dto.entity.RequestEntityStatusResDto;
import org.omnione.did.tas.v1.admin.service.EntityManagementService;
import org.omnione.did.tas.v1.admin.service.JwtService;
import org.omnione.did.tas.v1.admin.controller.EntityManagementController;
import org.omnione.did.tas.v1.common.service.ApiLogService;
import org.omnione.did.tas.v1.common.service.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EntityManagementController.class)
@Import(SecurityConfig.class)
class EntityRegistrationAccessTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityManagementService entityManagementService;
    @MockBean
    private SetupService setupService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ApiLogService apiLogService;

    @Test
    void entityStatusIsPublicWhileOtherAdminDataStaysProtected() throws Exception {
        when(entityManagementService.requestEntityStatus("did:omn:issuer"))
                .thenReturn(new RequestEntityStatusResDto(EntityRegistrationStatus.COMPLETED));

        mockMvc.perform(get("/tas/admin/v1/entities/request-status").param("did", "did:omn:issuer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
        mockMvc.perform(get("/tas/admin/v1/entities/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void entityDidRegistrationIsPublicWhileOtherAdminWritesStayProtected() throws Exception {
        String body = "{\"didDoc\":\"encoded\",\"name\":\"Issuer\",\"role\":\"ISSUER\"," +
                "\"serverUrl\":\"https://issuer.example\",\"certificateUrl\":\"https://issuer.example/cert\"}";
        when(entityManagementService.registerDidFromEntity(any())).thenReturn(new org.omnione.did.tas.v1.common.dto.EmptyResDto());

        mockMvc.perform(post("/tas/admin/v1/entities/register-did/public")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(post("/tas/admin/v1/entities/register-simple")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }
}
