package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListVcSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : ListVcSchemaRepositoryAdmin
 * @since : 3/10/25
 */
public interface ListVcSchemaRepositoryAdmin {
    Page<ListVcSchema> searchListVcSchemas(String searchKey, String searchValue, Pageable pageable);
}
