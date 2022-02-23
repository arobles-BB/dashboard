package com.bloobirds.dashboards.datamodel.service;

import com.bloobirds.dashboards.datamodel.CallLog;
import com.bloobirds.dashboards.datamodel.repo.CallLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.stream.Stream;

@Service
@Transactional // to avoid LazyInitializationException in attributes class
public class CallLogService {

    private final CallLogRepository repo;

    public CallLogService(CallLogRepository repository) {
        this.repo=repository;
    }

    public Stream<CallLog> findAll(int page, int pageSize) {
        return repo.findAll(PageRequest.of(page,pageSize)).stream();
    }

    public Stream<CallLog> findAll(LocalDate filter, int page, int pageSize) {
        return repo.findByDateCall(filter, PageRequest.of(page,pageSize)).stream();
    }

    public CallLog save(CallLog callLog) {
        return repo.save(callLog);
    }
}
