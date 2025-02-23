package org.omnione.did.base.db.repository;


import org.omnione.did.base.db.constant.EntityStatus;
import org.omnione.did.base.db.domain.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityRepositoryAdmin {
    Page<Entity> searchEntities(String searchKey, String searchValue, Pageable pageable);
}
