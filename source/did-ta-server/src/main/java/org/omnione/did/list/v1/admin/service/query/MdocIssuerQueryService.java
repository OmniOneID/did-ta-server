package org.omnione.did.list.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.MdocIssuerStatus;
import org.omnione.did.base.db.domain.Entity;
import org.omnione.did.base.db.domain.ListMdocIssuer;
import org.omnione.did.base.db.domain.ListMdocIssuerDocType;
import org.omnione.did.base.db.repository.EntityRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerDocTypeRepository;
import org.omnione.did.base.db.repository.ListMdocIssuerRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.admin.dto.mdoc.MdocIssuerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MdocIssuerQueryService {
    private final ListMdocIssuerRepository repository;
    private final ListMdocIssuerDocTypeRepository docTypeRepository;
    private final EntityRepository entityRepository;

    public ListMdocIssuer findById(Long id) {
        if (id == null) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_NOT_FOUND);
        }
        return repository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.MDOC_ISSUER_NOT_FOUND));
    }

    public MdocIssuerDto toDto(ListMdocIssuer issuer) {
        Entity entity = entityRepository.findById(issuer.getEntityId()).orElse(null);
        return MdocIssuerDto.from(issuer, entity, docTypesOf(issuer.getId()));
    }

    public List<String> docTypesOf(Long issuerId) {
        return docTypeRepository.findAllByMdocIssuerIdOrderByIdAsc(issuerId).stream()
                .map(ListMdocIssuerDocType::getDocType)
                .toList();
    }

    /**
     * DID and name live in the entity table, so filtering happens in memory.
     * mDoc issuers are few; results are always ordered by creation time, newest first.
     */
    public Page<MdocIssuerDto> search(String searchValue, MdocIssuerStatus status, Pageable pageable) {
        String value = searchValue == null || searchValue.isBlank()
                ? null : searchValue.trim().toLowerCase(Locale.ROOT);
        List<ListMdocIssuer> issuers = status == null ? repository.findAll() : repository.findAllByStatus(status);
        Map<Long, Entity> entities = entitiesById(issuers);
        Map<Long, List<String>> docTypes = docTypesByIssuerId(issuers);

        List<MdocIssuerDto> matched = issuers.stream()
                .map(issuer -> MdocIssuerDto.from(issuer, entities.get(issuer.getEntityId()),
                        docTypes.getOrDefault(issuer.getId(), List.of())))
                .filter(dto -> value == null || contains(dto.getDid(), value) || contains(dto.getName(), value))
                .sorted(Comparator.comparing(MdocIssuerDto::getCreatedAt,
                                Comparator.nullsLast(Comparator.<Instant>reverseOrder()))
                        .thenComparing(MdocIssuerDto::getId, Comparator.reverseOrder()))
                .toList();

        if (pageable.isUnpaged()) {
            return new PageImpl<>(matched, pageable, matched.size());
        }
        int from = (int) Math.min(pageable.getOffset(), matched.size());
        int to = Math.min(from + pageable.getPageSize(), matched.size());
        return new PageImpl<>(matched.subList(from, to), pageable, matched.size());
    }

    private Map<Long, Entity> entitiesById(List<ListMdocIssuer> issuers) {
        if (issuers.isEmpty()) {
            return Map.of();
        }
        Set<Long> entityIds = issuers.stream().map(ListMdocIssuer::getEntityId).collect(Collectors.toSet());
        return entityRepository.findAllById(entityIds).stream()
                .collect(Collectors.toMap(Entity::getId, Function.identity()));
    }

    private Map<Long, List<String>> docTypesByIssuerId(List<ListMdocIssuer> issuers) {
        if (issuers.isEmpty()) {
            return Map.of();
        }
        List<Long> issuerIds = issuers.stream().map(ListMdocIssuer::getId).toList();
        return docTypeRepository.findAllByMdocIssuerIdInOrderByIdAsc(issuerIds).stream()
                .collect(Collectors.groupingBy(ListMdocIssuerDocType::getMdocIssuerId, LinkedHashMap::new,
                        Collectors.mapping(ListMdocIssuerDocType::getDocType, Collectors.toList())));
    }

    private static boolean contains(String text, String lowerCaseValue) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(lowerCaseValue);
    }
}
