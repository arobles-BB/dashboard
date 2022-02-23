package com.bloobirds.dashboards.datamodel.repo;

import com.bloobirds.dashboards.datamodel.CallLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CallLogRepository extends JpaRepository<CallLog, Integer> {

    Page<CallLog> findByDateCall(LocalDate filter, Pageable pageable);
}

