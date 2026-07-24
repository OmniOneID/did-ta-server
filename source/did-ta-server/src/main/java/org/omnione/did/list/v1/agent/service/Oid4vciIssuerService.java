package org.omnione.did.list.v1.agent.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;
import org.omnione.did.base.db.domain.ListOid4vciIssuerStatusHistory;
import org.omnione.did.base.db.repository.ListOid4vciIssuerRepository;
import org.omnione.did.base.db.repository.ListOid4vciIssuerStatusHistoryRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.agent.dto.oid4vci.Oid4vciIssuerPublicDto;
import org.omnione.did.list.v1.agent.dto.oid4vci.Oid4vciIssuerListResDto;
import org.omnione.did.list.v1.agent.dto.oid4vci.Oid4vciIssuerRegistrationResDto;
import org.omnione.did.list.v1.agent.dto.oid4vci.RegisterOid4vciIssuerReqDto;
import org.omnione.did.list.v1.common.Oid4vciUriValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class Oid4vciIssuerService {
    private final ListOid4vciIssuerRepository repository;
    private final ListOid4vciIssuerStatusHistoryRepository historyRepository;

    public Oid4vciIssuerRegistrationResDto register(RegisterOid4vciIssuerReqDto request) {
        String credentialIssuer = Oid4vciUriValidator.credentialIssuer(request.getCredentialIssuer());
        if (repository.existsByCredentialIssuer(credentialIssuer)) {
            throw new OpenDidException(ErrorCode.OID4VCI_ISSUER_ALREADY_EXISTS);
        }
        String metadataUri = request.getCredentialIssuerMetadataUri() == null
                || request.getCredentialIssuerMetadataUri().isBlank()
                ? Oid4vciUriValidator.defaultMetadataUri(credentialIssuer)
                : Oid4vciUriValidator.metadataUri(request.getCredentialIssuerMetadataUri());
        String userInitiationUri = Oid4vciUriValidator.userInitiationUri(request.getUserInitiationUri());
        try {
            ListOid4vciIssuer saved = repository.saveAndFlush(ListOid4vciIssuer.builder()
                    .credentialIssuer(credentialIssuer)
                    .credentialIssuerMetadataUri(metadataUri)
                    .userInitiationUri(userInitiationUri)
                    .status(Oid4vciIssuerStatus.REQUESTED)
                    .build());
            historyRepository.save(ListOid4vciIssuerStatusHistory.builder()
                    .issuerId(saved.getId())
                    .previousStatus(null)
                    .newStatus(Oid4vciIssuerStatus.REQUESTED)
                    .reason("Registration requested")
                    .changedAt(Instant.now())
                    .build());
            return Oid4vciIssuerRegistrationResDto.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new OpenDidException(ErrorCode.OID4VCI_ISSUER_ALREADY_EXISTS);
        }
    }

    public Oid4vciIssuerListResDto findActive() {
        List<Oid4vciIssuerPublicDto> items =
                repository.findAllByStatusOrderByCreatedAtAsc(Oid4vciIssuerStatus.ACTIVE).stream()
                        .map(Oid4vciIssuerPublicDto::from)
                        .toList();
        return Oid4vciIssuerListResDto.builder()
                .count(items.size())
                .items(items)
                .build();
    }

    public Oid4vciIssuerPublicDto findActive(String credentialIssuer) {
        String normalized = Oid4vciUriValidator.credentialIssuer(credentialIssuer);
        return repository.findByCredentialIssuerAndStatus(normalized, Oid4vciIssuerStatus.ACTIVE)
                .map(Oid4vciIssuerPublicDto::from)
                .orElseThrow(() -> new OpenDidException(ErrorCode.OID4VCI_ISSUER_NOT_FOUND));
    }
}
