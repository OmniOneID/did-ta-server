package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ListVcPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Please explain the class!!
 *
 * @author : yklee0911
 * @fileName : ListVcPlanRepositoryAdmin
 * @since : 3/11/25
 */
public interface ListVcPlanRepositoryAdmin {
    public Page<ListVcPlan> searchVcPlans(String searchKey, String searchValue, Pageable pageable);
}
