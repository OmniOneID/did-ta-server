package org.omnione.did.list.v1.agent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds the public mDoc trusted issuer list: ACTIVE grants whose entity is an enrolled Issuer
 * (role ISSUER, status COMPLETED). Read-only.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MdocIssuerService {
    private final ListMdocIssuerRepository repository;
    private final ListMdocIssuerDocTypeRepository docTypeRepository;
    private final EntityRepository entityRepository;

    public MdocIssuerListResDto findActive() {
        List<ListMdocIssuer> active = repository.findAllByStatusOrderByCreatedAtAsc(MdocIssuerStatus.ACTIVE);
        Map<Long, Entity> entities = entitiesById(active);
        Map<Long, List<String>> docTypes = docTypesByIssuerId(active);

        List<MdocIssuerPublicDto> items = new ArrayList<>();
        for (ListMdocIssuer issuer : active) {
            Entity entity = entities.get(issuer.getEntityId());
            List<String> issuerDocTypes = docTypes.getOrDefault(issuer.getId(), List.of());
            if (!isEnrolledIssuer(entity) || issuerDocTypes.isEmpty()) {
                log.warn("Skipping mDoc issuer id={} entityId={}: entity is not an enrolled Issuer or has no docTypes",
                        issuer.getId(), issuer.getEntityId());
                continue;
            }
            items.add(MdocIssuerPublicDto.builder()
                    .did(entity.getDid())
                    .name(entity.getName())
                    .docTypes(issuerDocTypes)
                    .build());
        }

        return MdocIssuerListResDto.builder()
                .updatedAt(formatUpdatedAt(latestChange()))
                .count(items.size())
                .items(items)
                .build();
    }

    /** Last change to the list: grant created/updated (status) or docTypes replaced. Now if nothing registered yet. */
    private Instant latestChange() {
        return Stream.of(repository.findLatestCreatedAt(),
                        repository.findLatestUpdatedAt(),
                        docTypeRepository.findLatestCreatedAt())
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElseGet(Instant::now);
    }

    static String formatUpdatedAt(Instant instant) {
        return DateTimeFormatter.ISO_INSTANT.format(instant.truncatedTo(ChronoUnit.SECONDS));
    }

    private static boolean isEnrolledIssuer(Entity entity) {
        return entity != null && entity.getRole() == Role.ISSUER && entity.getStatus() == EntityStatus.COMPLETED;
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
}
