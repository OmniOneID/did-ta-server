package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListCredentialSchema;
import org.omnione.did.base.db.domain.ListVcSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : ListCredentialSchemaRepositoryAdmin
 * @since : 3/10/25
 */
public interface ListCredentialSchemaRepositoryAdmin {
    Page<ListCredentialSchema> searchListCredentialSchemas(String searchKey, String searchValue, Pageable pageable);
}
