package com.bloobirds.dashboards.datamodel.repo;

import com.bloobirds.dashboards.datamodel.Contact;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    // @todo need to work for a fetch join
//    @Query(value = "SELECT c FROM Contact AS c JOIN c.assignTo")
//    @Cacheable("findAllUsersWithAssignTo")
//    Page<Contact> findAllUsersWithAssignTo(Pageable pageable);
}
