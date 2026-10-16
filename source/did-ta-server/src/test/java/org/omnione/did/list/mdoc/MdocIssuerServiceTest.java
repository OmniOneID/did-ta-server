package org.omnione.did.list.mdoc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListMdocIssuer;
import org.omnione.did.base.db.domain.ListMdocIssuerDocType;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerDocTypeRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerRepository;
import org.omnione.did.list.v1.agent.dto.mdoc.MdocIssuerListResDto;
import org.omnione.did.list.v1.agent.dto.mdoc.MdocIssuerPublicDto;
import org.omnione.did.list.v1.agent.service.MdocIssuerService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MdocIssuerServiceTest {
    private static final String MDL = "org.iso.18013.5.1.mDL";
    private static final String PID = "eu.europa.ec.eudi.pid.1";

    @Mock ListMdocIssuerRepository repository;
    @Mock ListMdocIssuerDocTypeRepository docTypeRepository;
    @Mock EntityRepository entityRepository;
    private MdocIssuerService service;

    @BeforeEach
    void setUp() {
        service = new MdocIssuerService(repository, docTypeRepository, entityRepository);
    }

    @Test
    void listsOnlyActiveGrantsOfEnrolledIssuersWithDocTypes() {
        ListMdocIssuer enrolled = grant(10L, 1L);
        ListMdocIssuer notAnIssuer = grant(11L, 2L);
        ListMdocIssuer withoutDocTypes = grant(12L, 3L);
        when(repository.findAllByStatusOrderByCreatedAtAsc(MdocIssuerStatus.ACTIVE))
                .thenReturn(List.of(enrolled, notAnIssuer, withoutDocTypes));
        when(entityRepository.findAllById(any())).thenReturn(List.of(
                entity(1L, Role.ISSUER, EntityStatus.COMPLETED),
                entity(2L, Role.VERIFIER, EntityStatus.COMPLETED),
                entity(3L, Role.ISSUER, EntityStatus.COMPLETED)));
        when(docTypeRepository.findAllByMdocIssuerIdInOrderByIdAsc(any())).thenReturn(List.of(
                docType(10L, MDL), docType(10L, PID), docType(11L, MDL)));
        when(repository.findLatestCreatedAt()).thenReturn(Instant.parse("2026-10-15T08:00:00Z"));
        when(repository.findLatestUpdatedAt()).thenReturn(Instant.parse("2026-10-15T09:30:15.123456Z"));
        when(docTypeRepository.findLatestCreatedAt()).thenReturn(null);

        MdocIssuerListResDto result = service.findActive();

        assertEquals(1, result.getCount());
        MdocIssuerPublicDto item = result.getItems().get(0);
        assertEquals("did:omn:issuer1", item.getDid());
        assertEquals("Issuer 1", item.getName());
        assertEquals(List.of(MDL, PID), item.getDocTypes());
        assertEquals("2026-10-15T09:30:15Z", result.getUpdatedAt());
    }

    @Test
    void emptyListStillHasUpdatedAtAndSkipsLookups() {
        when(repository.findAllByStatusOrderByCreatedAtAsc(MdocIssuerStatus.ACTIVE)).thenReturn(List.of());

        MdocIssuerListResDto result = service.findActive();

        assertEquals(0, result.getCount());
        assertTrue(result.getItems().isEmpty());
        assertNotNull(Instant.parse(result.getUpdatedAt()));
        verify(entityRepository, never()).findAllById(any());
        verify(docTypeRepository, never()).findAllByMdocIssuerIdInOrderByIdAsc(any());
    }

    private static ListMdocIssuer grant(Long id, Long entityId) {
        return ListMdocIssuer.builder().id(id).entityId(entityId).status(MdocIssuerStatus.ACTIVE).build();
    }

    private static Entity entity(Long id, Role role, EntityStatus status) {
        return Entity.builder().id(id).did("did:omn:issuer" + id).name("Issuer " + id)
                .role(role).status(status).build();
    }

    private static ListMdocIssuerDocType docType(Long issuerId, String docType) {
        return ListMdocIssuerDocType.builder().mdocIssuerId(issuerId).docType(docType)
                .createdAt(Instant.parse("2026-10-15T08:00:00Z")).build();
    }
}
