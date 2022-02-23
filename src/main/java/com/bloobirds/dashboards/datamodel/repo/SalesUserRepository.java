package com.bloobirds.dashboards.datamodel.repo;

import com.bloobirds.dashboards.datamodel.Contact;
import com.bloobirds.dashboards.datamodel.SalesUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesUserRepository extends JpaRepository<SalesUser, Integer> {
}
