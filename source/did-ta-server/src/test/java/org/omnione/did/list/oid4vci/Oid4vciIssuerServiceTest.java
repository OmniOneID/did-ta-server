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
import org.omnione.did.list.v1.agent.dto.oid4vci.RegisterOid4vciIssuerReqDto;
import org.omnione.did.list.v1.agent.service.Oid4vciIssuerService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Oid4vciIssuerServiceTest {
    @Mock ListOid4vciIssuerRepository repository;
    @Mock ListOid4vciIssuerStatusHistoryRepository historyRepository;
    private Oid4vciIssuerService service;

    @BeforeEach
    void setUp() {
        service = new Oid4vciIssuerService(repository, historyRepository);
    }

    @Test
    void registersAsRequestedWithDefaultMetadataUri() {
        RegisterOid4vciIssuerReqDto request = mock(RegisterOid4vciIssuerReqDto.class);
        when(request.getCredentialIssuer()).thenReturn("https://issuer.example.com");
        when(request.getCredentialIssuerMetadataUri()).thenReturn(null);
        when(request.getUserInitiationUri()).thenReturn("https://issuer.example.com/issue/start");
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            ListOid4vciIssuer issuer = invocation.getArgument(0);
            issuer.setId(7L);
            return issuer;
        });

        var response = service.register(request);

        assertEquals(7L, response.getId());
        assertEquals(Oid4vciIssuerStatus.REQUESTED, response.getStatus());
        verify(repository).saveAndFlush(argThat(issuer ->
                issuer.getStatus() == Oid4vciIssuerStatus.REQUESTED
                        && issuer.getCredentialIssuerMetadataUri().equals(
                        "https://issuer.example.com/.well-known/openid-credential-issuer")));
    }

    @Test
    void rejectsDuplicateIssuer() {
        RegisterOid4vciIssuerReqDto request = mock(RegisterOid4vciIssuerReqDto.class);
        when(request.getCredentialIssuer()).thenReturn("https://issuer.example.com");
        when(repository.existsByCredentialIssuer("https://issuer.example.com")).thenReturn(true);

        OpenDidException exception = assertThrows(OpenDidException.class, () -> service.register(request));
        assertEquals(ErrorCode.OID4VCI_ISSUER_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void walletListReturnsOnlyRepositoryActiveResults() {
        ListOid4vciIssuer active = ListOid4vciIssuer.builder()
                .credentialIssuer("https://active.example.com")
                .credentialIssuerMetadataUri("https://active.example.com/metadata")
                .userInitiationUri("https://active.example.com/start")
                .status(Oid4vciIssuerStatus.ACTIVE)
                .build();
        when(repository.findAllByStatusOrderByCreatedAtAsc(Oid4vciIssuerStatus.ACTIVE))
                .thenReturn(List.of(active));

        var result = service.findActive();

        assertEquals(1, result.getCount());
        assertEquals("https://active.example.com", result.getItems().getFirst().getCredentialIssuer());
        verify(repository).findAllByStatusOrderByCreatedAtAsc(Oid4vciIssuerStatus.ACTIVE);
    }
}
