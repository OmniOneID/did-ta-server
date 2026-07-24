package org.omnione.did.list.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;
import org.omnione.did.base.db.domain.ListOid4vciIssuerStatusHistory;
import org.omnione.did.base.db.repository.ListOid4vciIssuerRepository;
import org.omnione.did.base.db.repository.ListOid4vciIssuerStatusHistoryRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.oid4vci.*;
import org.omnione.did.list.v1.admin.service.query.Oid4vciIssuerQueryService;
import org.omnione.did.list.v1.common.Oid4vciUriValidator;
import org.omnione.did.tas.v1.common.dto.EmptyResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class Oid4vciIssuerManagementService {
    private final ListOid4vciIssuerRepository repository;
    private final ListOid4vciIssuerStatusHistoryRepository historyRepository;
    private final Oid4vciIssuerQueryService queryService;

    public Page<Oid4vciIssuerDto> search(String searchValue, Oid4vciIssuerStatus status, Pageable pageable) {
        return queryService.search(searchValue, status, pageable);
    }

    public Oid4vciIssuerDto findById(Long id) {
        return Oid4vciIssuerDto.from(queryService.findById(id));
    }

    public EmptyResDto update(UpdateOid4vciIssuerReqDto request) {
        ListOid4vciIssuer issuer = queryService.findById(request.getId());
        String credentialIssuer = Oid4vciUriValidator.credentialIssuer(request.getCredentialIssuer());
        if (!issuer.getCredentialIssuer().equals(credentialIssuer)
                && repository.existsByCredentialIssuer(credentialIssuer)) {
            throw new OpenDidException(ErrorCode.OID4VCI_ISSUER_ALREADY_EXISTS);
        }
        issuer.setCredentialIssuer(credentialIssuer);
        issuer.setCredentialIssuerMetadataUri(resolveMetadataUri(
                credentialIssuer, request.getCredentialIssuerMetadataUri()));
        issuer.setUserInitiationUri(Oid4vciUriValidator.userInitiationUri(request.getUserInitiationUri()));
        repository.save(issuer);
        return new EmptyResDto();
    }

    public EmptyResDto changeStatus(Long id, ChangeOid4vciIssuerStatusReqDto request) {
        ListOid4vciIssuer issuer = queryService.findById(id);
        Oid4vciIssuerStatus target = request.getStatus();
        if (target == null || !issuer.getStatus().canTransitionTo(target)) {
            throw new OpenDidException(ErrorCode.OID4VCI_STATUS_TRANSITION_INVALID);
        }
        String reason = request.getReason() == null ? null : request.getReason().trim();
        if (Oid4vciIssuerStatus.requiresReason(target) && (reason == null || reason.isBlank())) {
            throw new OpenDidException(ErrorCode.OID4VCI_STATUS_REASON_REQUIRED);
        }
        Oid4vciIssuerStatus previous = issuer.getStatus();
        issuer.setStatus(target);
        repository.save(issuer);
        historyRepository.save(ListOid4vciIssuerStatusHistory.builder()
                .issuerId(id)
                .previousStatus(previous)
                .newStatus(target)
                .reason(reason)
                .changedBy(null)
                .changedAt(Instant.now())
                .build());
        return new EmptyResDto();
    }

    public List<Oid4vciIssuerStatusHistoryDto> history(Long id) {
        queryService.findById(id);
        return historyRepository.findAllByIssuerIdOrderByChangedAtDesc(id).stream()
                .map(Oid4vciIssuerStatusHistoryDto::from)
                .toList();
    }

    private String resolveMetadataUri(String issuer, String metadataUri) {
        return metadataUri == null || metadataUri.isBlank()
                ? Oid4vciUriValidator.defaultMetadataUri(issuer)
                : Oid4vciUriValidator.metadataUri(metadataUri);
    }
}
