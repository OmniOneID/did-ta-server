package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListCredentialDefinition;
import org.omnione.did.base.db.domain.ListCredentialSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : ListCredentialDefinitionRepositoryAdmin
 * @since : 3/10/25
 */
public interface ListCredentialDefinitionRepositoryAdmin {
    Page<ListCredentialDefinition> searchListCredentialDefinition(String searchKey, String searchValue, Pageable pageable);
}
