package org.omnione.did.list.mdoc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import org.omnione.did.base.db.repository.ListMdocIssuerStatusHistoryRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.mdoc.ChangeMdocIssuerStatusReqDto;
import org.omnione.did.list.v1.admin.dto.mdoc.MdocIssuerDto;
import org.omnione.did.list.v1.admin.dto.mdoc.RegisterMdocIssuerReqDto;
import org.omnione.did.list.v1.admin.dto.mdoc.UpdateMdocIssuerReqDto;
import org.omnione.did.list.v1.admin.service.MdocIssuerManagementService;
import org.omnione.did.list.v1.admin.service.query.MdocIssuerQueryService;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient.IssuerMetadata;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MdocIssuerManagementServiceTest {
    private static final String MDL = "org.iso.18013.5.1.mDL";
    private static final String PID = "eu.europa.ec.eudi.pid.1";
    private static final String METADATA_URI = "http://issuer.local:8082/.well-known/openid-credential-issuer";

    @Mock ListMdocIssuerRepository repository;
    @Mock ListMdocIssuerDocTypeRepository docTypeRepository;
    @Mock ListMdocIssuerStatusHistoryRepository historyRepository;
    @Mock EntityRepository entityRepository;
    @Mock MdocIssuerQueryService queryService;
    @Mock MdocIssuerMetadataClient metadataClient;
    @Captor ArgumentCaptor<List<ListMdocIssuerDocType>> docTypesCaptor;
    private MdocIssuerManagementService service;

    @BeforeEach
    void setUp() {
        service = new MdocIssuerManagementService(
                repository, docTypeRepository, historyRepository, entityRepository, queryService, metadataClient);
    }

    @Test
    void registersEnrolledIssuerAsActiveWithDeclaredDocTypes() {
        when(entityRepository.findById(3L)).thenReturn(Optional.of(entity(3L, Role.ISSUER, EntityStatus.COMPLETED)));
        when(repository.findByEntityId(3L)).thenReturn(Optional.empty());
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL, PID));
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            ListMdocIssuer issuer = invocation.getArgument(0);
            issuer.setId(9L);
            return issuer;
        });
        RegisterMdocIssuerReqDto request = registerRequest(3L, List.of(" " + MDL + " ", PID, MDL));

        MdocIssuerDto response = service.register(request);

        assertEquals(9L, response.getId());
        assertEquals(MdocIssuerStatus.ACTIVE, response.getStatus());
        assertEquals("did:omn:issuer3", response.getDid());
        assertEquals(METADATA_URI, response.getMetadataUri());
        assertEquals(List.of(MDL, PID), response.getDocTypes());
        verify(docTypeRepository).saveAll(docTypesCaptor.capture());
        assertEquals(List.of(MDL, PID), docTypesCaptor.getValue().stream().map(ListMdocIssuerDocType::getDocType).toList());
        verify(historyRepository).save(argThat(history -> history.getPreviousStatus() == null
                && history.getNewStatus() == MdocIssuerStatus.ACTIVE && history.getMdocIssuerId() == 9L));
    }

    @Test
    void rejectsRegistrationWithDocTypeTheIssuerDoesNotDeclare() {
        when(entityRepository.findById(3L)).thenReturn(Optional.of(entity(3L, Role.ISSUER, EntityStatus.COMPLETED)));
        when(repository.findByEntityId(3L)).thenReturn(Optional.empty());
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL));
        RegisterMdocIssuerReqDto request = registerRequest(3L, List.of(MDL, PID));

        assertError(ErrorCode.MDOC_ISSUER_DOCTYPE_NOT_DECLARED, () -> service.register(request));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void rejectsEntityThatIsNotAnEnrolledIssuer() {
        when(entityRepository.findById(4L)).thenReturn(Optional.of(entity(4L, Role.VERIFIER, EntityStatus.COMPLETED)));
        when(entityRepository.findById(5L)).thenReturn(Optional.of(entity(5L, Role.ISSUER, EntityStatus.CERTIFICATE_VC_REQUIRED)));
        when(entityRepository.findById(6L)).thenReturn(Optional.empty());

        for (Long entityId : List.of(4L, 5L, 6L)) {
            RegisterMdocIssuerReqDto request = mock(RegisterMdocIssuerReqDto.class);
            when(request.getEntityId()).thenReturn(entityId);
            assertError(ErrorCode.MDOC_ISSUER_ENTITY_NOT_ELIGIBLE, () -> service.register(request));
        }
        verify(repository, never()).saveAndFlush(any());
        verifyNoInteractions(metadataClient);
    }

    @Test
    void rejectsDuplicateRegistrationWhileActiveOrSuspended() {
        when(entityRepository.findById(3L)).thenReturn(Optional.of(entity(3L, Role.ISSUER, EntityStatus.COMPLETED)));
        for (MdocIssuerStatus status : List.of(MdocIssuerStatus.ACTIVE, MdocIssuerStatus.SUSPENDED)) {
            when(repository.findByEntityId(3L)).thenReturn(Optional.of(issuer(9L, status, METADATA_URI)));
            RegisterMdocIssuerReqDto request = mock(RegisterMdocIssuerReqDto.class);
            when(request.getEntityId()).thenReturn(3L);

            assertError(ErrorCode.MDOC_ISSUER_ALREADY_EXISTS, () -> service.register(request));
        }
        verify(repository, never()).saveAndFlush(any());
        verifyNoInteractions(metadataClient);
    }

    @Test
    void reRegistersRevokedIssuerInPlace() {
        ListMdocIssuer revoked = issuer(9L, MdocIssuerStatus.REVOKED, null);
        when(entityRepository.findById(3L)).thenReturn(Optional.of(entity(3L, Role.ISSUER, EntityStatus.COMPLETED)));
        when(repository.findByEntityId(3L)).thenReturn(Optional.of(revoked));
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(PID));
        when(repository.saveAndFlush(revoked)).thenReturn(revoked);

        MdocIssuerDto response = service.register(registerRequest(3L, List.of(PID)));

        assertEquals(9L, response.getId());
        assertEquals(MdocIssuerStatus.ACTIVE, response.getStatus());
        assertEquals(METADATA_URI, response.getMetadataUri());
        assertEquals(List.of(PID), response.getDocTypes());
        verify(docTypeRepository).deleteAllByMdocIssuerId(9L);
        verify(docTypeRepository).saveAll(docTypesCaptor.capture());
        assertEquals(List.of(PID), docTypesCaptor.getValue().stream().map(ListMdocIssuerDocType::getDocType).toList());
        verify(historyRepository).save(argThat(history -> history.getPreviousStatus() == MdocIssuerStatus.REVOKED
                && history.getNewStatus() == MdocIssuerStatus.ACTIVE && "Re-registered by admin".equals(history.getReason())));
    }

    @Test
    void rejectsRegistrationWithoutDocTypes() {
        when(entityRepository.findById(3L)).thenReturn(Optional.of(entity(3L, Role.ISSUER, EntityStatus.COMPLETED)));
        when(repository.findByEntityId(3L)).thenReturn(Optional.empty());
        RegisterMdocIssuerReqDto request = mock(RegisterMdocIssuerReqDto.class);
        when(request.getEntityId()).thenReturn(3L);
        when(request.getDocTypes()).thenReturn(List.of());

        assertError(ErrorCode.MDOC_ISSUER_DOCTYPE_INVALID, () -> service.register(request));
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void updateIsNoOpWhenMetadataUriAndDocTypesAreUnchanged() {
        when(queryService.findById(9L)).thenReturn(issuer(9L, MdocIssuerStatus.ACTIVE, METADATA_URI));
        when(queryService.docTypesOf(9L)).thenReturn(List.of(MDL, PID));
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL, PID));

        service.update(updateRequest(9L, List.of(PID, MDL)));

        verify(repository, never()).save(any());
        verify(docTypeRepository, never()).deleteAllByMdocIssuerId(anyLong());
        verify(docTypeRepository, never()).saveAll(any());
    }

    @Test
    void updateReplacesDocTypesWhenChanged() {
        when(queryService.findById(9L)).thenReturn(issuer(9L, MdocIssuerStatus.SUSPENDED, METADATA_URI));
        when(queryService.docTypesOf(9L)).thenReturn(List.of(MDL));
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL, PID));

        service.update(updateRequest(9L, List.of(MDL, PID)));

        verify(docTypeRepository).deleteAllByMdocIssuerId(9L);
        verify(docTypeRepository).saveAll(docTypesCaptor.capture());
        assertEquals(List.of(MDL, PID), docTypesCaptor.getValue().stream().map(ListMdocIssuerDocType::getDocType).toList());
    }

    @Test
    void updateStoresMetadataUriForIssuerRegisteredWithoutOne() {
        ListMdocIssuer issuer = issuer(9L, MdocIssuerStatus.ACTIVE, null);
        when(queryService.findById(9L)).thenReturn(issuer);
        when(queryService.docTypesOf(9L)).thenReturn(List.of(MDL));
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL));

        service.update(updateRequest(9L, List.of(MDL)));

        assertEquals(METADATA_URI, issuer.getMetadataUri());
        verify(repository).save(issuer);
        verify(docTypeRepository, never()).deleteAllByMdocIssuerId(anyLong());
    }

    @Test
    void rejectsUpdateWithDocTypeTheIssuerDoesNotDeclare() {
        when(queryService.findById(9L)).thenReturn(issuer(9L, MdocIssuerStatus.ACTIVE, METADATA_URI));
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL));

        assertError(ErrorCode.MDOC_ISSUER_DOCTYPE_NOT_DECLARED, () -> service.update(updateRequest(9L, List.of(MDL, PID))));
        verify(repository, never()).save(any());
        verify(docTypeRepository, never()).deleteAllByMdocIssuerId(anyLong());
    }

    @Test
    void revokedIssuerCanStillBeEdited() {
        when(queryService.findById(9L)).thenReturn(issuer(9L, MdocIssuerStatus.REVOKED, METADATA_URI));
        when(queryService.docTypesOf(9L)).thenReturn(List.of(MDL));
        when(metadataClient.fetch(METADATA_URI)).thenReturn(metadata(MDL, PID));

        service.update(updateRequest(9L, List.of(PID)));

        verify(docTypeRepository).deleteAllByMdocIssuerId(9L);
        verify(docTypeRepository).saveAll(docTypesCaptor.capture());
        assertEquals(List.of(PID), docTypesCaptor.getValue().stream().map(ListMdocIssuerDocType::getDocType).toList());
    }

    @Test
    void rejectsForbiddenTransition() {
        when(queryService.findById(9L)).thenReturn(issuer(9L, MdocIssuerStatus.REVOKED, METADATA_URI));
        ChangeMdocIssuerStatusReqDto request = mock(ChangeMdocIssuerStatusReqDto.class);
        when(request.getStatus()).thenReturn(MdocIssuerStatus.ACTIVE);

        assertError(ErrorCode.MDOC_ISSUER_STATUS_TRANSITION_INVALID, () -> service.changeStatus(9L, request));
        verify(repository, never()).save(any());
    }

    @Test
    void requiresReasonForSuspension() {
        when(queryService.findById(9L)).thenReturn(issuer(9L, MdocIssuerStatus.ACTIVE, METADATA_URI));
        ChangeMdocIssuerStatusReqDto request = mock(ChangeMdocIssuerStatusReqDto.class);
        when(request.getStatus()).thenReturn(MdocIssuerStatus.SUSPENDED);
        when(request.getReason()).thenReturn(" ");

        assertError(ErrorCode.MDOC_ISSUER_STATUS_REASON_REQUIRED, () -> service.changeStatus(9L, request));
        verify(repository, never()).save(any());
    }

    @Test
    void suspendsWithReasonAndRecordsHistory() {
        ListMdocIssuer issuer = issuer(9L, MdocIssuerStatus.ACTIVE, METADATA_URI);
        when(queryService.findById(9L)).thenReturn(issuer);
        ChangeMdocIssuerStatusReqDto request = mock(ChangeMdocIssuerStatusReqDto.class);
        when(request.getStatus()).thenReturn(MdocIssuerStatus.SUSPENDED);
        when(request.getReason()).thenReturn(" key rotation ");

        service.changeStatus(9L, request);

        assertEquals(MdocIssuerStatus.SUSPENDED, issuer.getStatus());
        verify(repository).save(issuer);
        verify(historyRepository).save(argThat(history -> history.getPreviousStatus() == MdocIssuerStatus.ACTIVE
                && history.getNewStatus() == MdocIssuerStatus.SUSPENDED && "key rotation".equals(history.getReason())));
    }

    private static RegisterMdocIssuerReqDto registerRequest(Long entityId, List<String> docTypes) {
        RegisterMdocIssuerReqDto request = mock(RegisterMdocIssuerReqDto.class);
        when(request.getEntityId()).thenReturn(entityId);
        when(request.getMetadataUri()).thenReturn(METADATA_URI);
        when(request.getDocTypes()).thenReturn(docTypes);
        return request;
    }

    private static UpdateMdocIssuerReqDto updateRequest(Long id, List<String> docTypes) {
        UpdateMdocIssuerReqDto request = mock(UpdateMdocIssuerReqDto.class);
        when(request.getId()).thenReturn(id);
        when(request.getMetadataUri()).thenReturn(METADATA_URI);
        when(request.getDocTypes()).thenReturn(docTypes);
        return request;
    }

    private static IssuerMetadata metadata(String... docTypes) {
        return new IssuerMetadata(METADATA_URI, "http://issuer.local:8082", List.of(docTypes));
    }

    private static Entity entity(Long id, Role role, EntityStatus status) {
        return Entity.builder().id(id).did("did:omn:issuer" + id).name("Issuer " + id)
                .role(role).status(status).build();
    }

    private static ListMdocIssuer issuer(Long id, MdocIssuerStatus status, String metadataUri) {
        return ListMdocIssuer.builder().id(id).entityId(3L).metadataUri(metadataUri).status(status).build();
    }

    private static void assertError(ErrorCode expected, org.junit.jupiter.api.function.Executable executable) {
        OpenDidException exception = assertThrows(OpenDidException.class, executable);
        assertEquals(expected, exception.getErrorCode());
    }
}
