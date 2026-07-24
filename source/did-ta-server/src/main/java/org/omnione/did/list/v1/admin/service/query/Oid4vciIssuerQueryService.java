package org.omnione.did.list.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;
import org.omnione.did.base.db.domain.ListOid4vciIssuer;
import org.omnione.did.base.db.repository.ListOid4vciIssuerRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.oid4vci.Oid4vciIssuerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oid4vciIssuerQueryService {
    private final ListOid4vciIssuerRepository repository;

    public ListOid4vciIssuer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.OID4VCI_ISSUER_NOT_FOUND));
    }

    public Page<Oid4vciIssuerDto> search(String searchValue, Oid4vciIssuerStatus status, Pageable pageable) {
        String value = searchValue == null || searchValue.isBlank() ? null : searchValue.trim();
        Page<ListOid4vciIssuer> result;
        if (value == null && status == null) {
            result = repository.findAll(pageable);
        } else if (value == null) {
            result = repository.findAllByStatus(status, pageable);
        } else if (status == null) {
            result = repository.findByCredentialIssuerContainingIgnoreCase(value, pageable);
        } else {
            result = repository.findByCredentialIssuerContainingIgnoreCaseAndStatus(value, status, pageable);
        }
        return result.map(Oid4vciIssuerDto::from);
    }
}
