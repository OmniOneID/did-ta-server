package org.omnione.did.list.oid4vci;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.repository.ListOid4vciIssuerRepository;
import org.omnione.did.list.v1.admin.service.query.Oid4vciIssuerQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Oid4vciIssuerQueryServiceTest {
    @Mock
    private ListOid4vciIssuerRepository repository;
    private Oid4vciIssuerQueryService service;

    @BeforeEach
    void setUp() {
        service = new Oid4vciIssuerQueryService(repository);
    }

    @Test
    void usesUnfilteredQueryWhenOptionalFiltersAreNull() {
        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        service.search(null, null, pageable);

        verify(repository).findAll(pageable);
    }

    @Test
    void selectsQueryMatchingProvidedFilters() {
        var pageable = PageRequest.of(0, 10);
        when(repository.findAllByStatus(Oid4vciIssuerStatus.ACTIVE, pageable))
                .thenReturn(Page.empty(pageable));
        when(repository.findByCredentialIssuerContainingIgnoreCase("example", pageable))
                .thenReturn(Page.empty(pageable));
        when(repository.findByCredentialIssuerContainingIgnoreCaseAndStatus(
                "example", Oid4vciIssuerStatus.ACTIVE, pageable))
                .thenReturn(Page.empty(pageable));

        service.search(null, Oid4vciIssuerStatus.ACTIVE, pageable);
        service.search(" example ", null, pageable);
        service.search("example", Oid4vciIssuerStatus.ACTIVE, pageable);

        verify(repository).findAllByStatus(Oid4vciIssuerStatus.ACTIVE, pageable);
        verify(repository).findByCredentialIssuerContainingIgnoreCase("example", pageable);
        verify(repository).findByCredentialIssuerContainingIgnoreCaseAndStatus(
                "example", Oid4vciIssuerStatus.ACTIVE, pageable);
    }
}
