package org.omnione.did.list.oid4vci;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;
import org.omnione.did.base.db.repository.ListOid4vciIssuerRepository;
import org.omnione.did.base.db.repository.ListOid4vciIssuerStatusHistoryRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.oid4vci.ChangeOid4vciIssuerStatusReqDto;
import org.omnione.did.list.v1.admin.service.Oid4vciIssuerManagementService;
import org.omnione.did.list.v1.admin.service.query.Oid4vciIssuerQueryService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Oid4vciIssuerManagementServiceTest {
    @Mock ListOid4vciIssuerRepository repository;
    @Mock ListOid4vciIssuerStatusHistoryRepository historyRepository;
    @Mock Oid4vciIssuerQueryService queryService;
    private Oid4vciIssuerManagementService service;

    @BeforeEach
    void setUp() {
        service = new Oid4vciIssuerManagementService(repository, historyRepository, queryService);
    }

    @Test
    void rejectsForbiddenTransition() {
        ListOid4vciIssuer issuer = ListOid4vciIssuer.builder()
                .id(1L).status(Oid4vciIssuerStatus.REJECTED).build();
        ChangeOid4vciIssuerStatusReqDto request = mock(ChangeOid4vciIssuerStatusReqDto.class);
        when(queryService.findById(1L)).thenReturn(issuer);
        when(request.getStatus()).thenReturn(Oid4vciIssuerStatus.ACTIVE);

        OpenDidException exception = assertThrows(OpenDidException.class,
                () -> service.changeStatus(1L, request));

        assertEquals(ErrorCode.OID4VCI_STATUS_TRANSITION_INVALID, exception.getErrorCode());
        verify(repository, never()).save(any());
    }

    @Test
    void requiresReasonForSuspension() {
        ListOid4vciIssuer issuer = ListOid4vciIssuer.builder()
                .id(1L).status(Oid4vciIssuerStatus.ACTIVE).build();
        ChangeOid4vciIssuerStatusReqDto request = mock(ChangeOid4vciIssuerStatusReqDto.class);
        when(queryService.findById(1L)).thenReturn(issuer);
        when(request.getStatus()).thenReturn(Oid4vciIssuerStatus.SUSPENDED);
        when(request.getReason()).thenReturn(" ");

        OpenDidException exception = assertThrows(OpenDidException.class,
                () -> service.changeStatus(1L, request));

        assertEquals(ErrorCode.OID4VCI_STATUS_REASON_REQUIRED, exception.getErrorCode());
        verify(repository, never()).save(any());
    }
}
