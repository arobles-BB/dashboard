package com.bloobirds.dashboards.datamodel.repo;

import com.bloobirds.dashboards.datamodel.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

}
