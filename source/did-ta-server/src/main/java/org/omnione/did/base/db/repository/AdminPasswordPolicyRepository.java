package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.AdminPasswordPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminPasswordPolicyRepository extends JpaRepository<AdminPasswordPolicy, Long> {
    Optional<AdminPasswordPolicy> findTop1ByOrderByIdAsc();
}
