package org.omnione.did.list.v1.admin.service;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.base.db.constant.Role;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListMdocIssuer;
import org.omnione.did.base.db.domain.ListMdocIssuerDocType;
import org.omnione.did.base.db.domain.ListMdocIssuerStatusHistory;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerDocTypeRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerStatusHistoryRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.mdoc.*;
import org.omnione.did.list.v1.admin.service.query.MdocIssuerQueryService;
import org.omnione.did.list.v1.common.MdocDocTypeValidator;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient.IssuerMetadata;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Admin management of mDoc trusted issuers. Only touches the list_mdoc_issuer* tables;
 * the entity table is read, never modified.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MdocIssuerManagementService {
    private static final String REGISTERED_REASON = "Registered by admin";
    private static final String RE_REGISTERED_REASON = "Re-registered by admin";

    private final ListMdocIssuerRepository repository;
    private final ListMdocIssuerDocTypeRepository docTypeRepository;
    private final ListMdocIssuerStatusHistoryRepository historyRepository;
    private final EntityRepository entityRepository;
    private final MdocIssuerQueryService queryService;
    private final MdocIssuerMetadataClient metadataClient;

    /** mso_mdoc docTypes declared in the issuer's OID4VCI metadata; the admin screen offers only these. */
    public MdocIssuerMetadataDto loadMetadata(String metadataUri) {
        return MdocIssuerMetadataDto.from(metadataClient.fetch(metadataUri));
    }

    public Page<MdocIssuerDto> search(String searchValue, MdocIssuerStatus status, Pageable pageable) {
        return queryService.search(searchValue, status, pageable);
    }

    public MdocIssuerDto findById(Long id) {
        return queryService.toDto(queryService.findById(id));
    }

    /**
     * Registers an Issuer. An Issuer has one row: an ACTIVE/SUSPENDED one is a duplicate,
     * a REVOKED one is re-registered in place so its status history stays continuous.
     */
    public MdocIssuerDto register(RegisterMdocIssuerReqDto request) {
        Entity entity = findEligibleEntity(request.getEntityId());
        Optional<ListMdocIssuer> existing = repository.findByEntityId(entity.getId());
        if (existing.isPresent() && existing.get().getStatus() != MdocIssuerStatus.REVOKED) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_ALREADY_EXISTS);
        }
        List<String> docTypes = MdocDocTypeValidator.docTypes(request.getDocTypes());
        IssuerMetadata metadata = metadataClient.fetch(request.getMetadataUri());
        ensureDeclared(docTypes, metadata);

        if (existing.isPresent()) {
            return reRegister(existing.get(), entity, metadata, docTypes);
        }

        ListMdocIssuer saved;
        try {
            saved = repository.saveAndFlush(ListMdocIssuer.builder()
                    .entityId(entity.getId())
                    .metadataUri(metadata.metadataUri())
                    .status(MdocIssuerStatus.ACTIVE)
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_ALREADY_EXISTS);
        }
        saveDocTypes(saved.getId(), docTypes);
        historyRepository.save(ListMdocIssuerStatusHistory.builder()
                .mdocIssuerId(saved.getId())
                .previousStatus(null)
                .newStatus(MdocIssuerStatus.ACTIVE)
                .reason(REGISTERED_REASON)
                .changedBy(null)
                .changedAt(Instant.now())
                .build());
        return MdocIssuerDto.from(saved, entity, docTypes);
    }

    private MdocIssuerDto reRegister(ListMdocIssuer issuer, Entity entity, IssuerMetadata metadata, List<String> docTypes) {
        MdocIssuerStatus previous = issuer.getStatus();
        issuer.setStatus(MdocIssuerStatus.ACTIVE);
        issuer.setMetadataUri(metadata.metadataUri());
        ListMdocIssuer saved = repository.saveAndFlush(issuer);
        docTypeRepository.deleteAllByMdocIssuerId(saved.getId());
        saveDocTypes(saved.getId(), docTypes);
        historyRepository.save(ListMdocIssuerStatusHistory.builder()
                .mdocIssuerId(saved.getId())
                .previousStatus(previous)
                .newStatus(MdocIssuerStatus.ACTIVE)
                .reason(RE_REGISTERED_REASON)
                .changedBy(null)
                .changedAt(Instant.now())
                .build());
        return MdocIssuerDto.from(saved, entity, docTypes);
    }

    /** Metadata URL and docTypes can be edited in any status; docTypes are re-checked against the metadata. */
    public EmptyResDto update(UpdateMdocIssuerReqDto request) {
        ListMdocIssuer issuer = queryService.findById(request.getId());
        List<String> docTypes = MdocDocTypeValidator.docTypes(request.getDocTypes());
        IssuerMetadata metadata = metadataClient.fetch(request.getMetadataUri());
        ensureDeclared(docTypes, metadata);

        if (!metadata.metadataUri().equals(issuer.getMetadataUri())) {
            issuer.setMetadataUri(metadata.metadataUri());
            repository.save(issuer);
        }
        if (new HashSet<>(queryService.docTypesOf(issuer.getId())).equals(new HashSet<>(docTypes))) {
            return new EmptyResDto();
        }
        // Replace the whole set: new rows carry a fresh created_at, which moves the public list's updatedAt.
        docTypeRepository.deleteAllByMdocIssuerId(issuer.getId());
        saveDocTypes(issuer.getId(), docTypes);
        return new EmptyResDto();
    }

    public EmptyResDto changeStatus(Long id, ChangeMdocIssuerStatusReqDto request) {
        ListMdocIssuer issuer = queryService.findById(id);
        MdocIssuerStatus target = request.getStatus();
        if (target == null || !issuer.getStatus().canTransitionTo(target)) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_STATUS_TRANSITION_INVALID);
        }
        String reason = request.getReason() == null ? null : request.getReason().trim();
        if (MdocIssuerStatus.requiresReason(target) && (reason == null || reason.isBlank())) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_STATUS_REASON_REQUIRED);
        }
        MdocIssuerStatus previous = issuer.getStatus();
        issuer.setStatus(target);
        repository.save(issuer);
        historyRepository.save(ListMdocIssuerStatusHistory.builder()
                .mdocIssuerId(id)
                .previousStatus(previous)
                .newStatus(target)
                .reason(reason)
                .changedBy(null)
                .changedAt(Instant.now())
                .build());
        return new EmptyResDto();
    }

    public List<MdocIssuerStatusHistoryDto> history(Long id) {
        queryService.findById(id);
        return historyRepository.findAllByMdocIssuerIdOrderByChangedAtDesc(id).stream()
                .map(MdocIssuerStatusHistoryDto::from)
                .toList();
    }

    private Entity findEligibleEntity(Long entityId) {
        if (entityId == null) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_ENTITY_NOT_ELIGIBLE);
        }
        Entity entity = entityRepository.findById(entityId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.MDOC_ISSUER_ENTITY_NOT_ELIGIBLE));
        if (entity.getRole() != Role.ISSUER || entity.getStatus() != EntityStatus.COMPLETED) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_ENTITY_NOT_ELIGIBLE);
        }
        return entity;
    }

    /** Granting a docType the issuer does not issue would widen what readers trust, so only declared ones are allowed. */
    private static void ensureDeclared(List<String> docTypes, IssuerMetadata metadata) {
        if (!new HashSet<>(metadata.docTypes()).containsAll(docTypes)) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_DOCTYPE_NOT_DECLARED);
        }
    }

    private void saveDocTypes(Long issuerId, List<String> docTypes) {
        Instant now = Instant.now();
        docTypeRepository.saveAll(docTypes.stream()
                .map(docType -> ListMdocIssuerDocType.builder()
                        .mdocIssuerId(issuerId)
                        .docType(docType)
                        .createdAt(now)
                        .build())
                .toList());
    }
}
