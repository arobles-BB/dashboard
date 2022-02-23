package com.bloobirds.dashboards.datamodel.service;

import com.bloobirds.dashboards.datamodel.SalesUser;
import com.bloobirds.dashboards.datamodel.repo.SalesUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class SalesUserService {
    private final SalesUserRepository repo;

    public SalesUserService(SalesUserRepository repository) {
        this.repo=repository;
    }
    public Stream<SalesUser> findAll(int page, int pageSize) {
        return repo.findAll(PageRequest.of(page,pageSize)).stream();
    }

    public List<SalesUser> findAll() {
        return repo.findAll();
    }
    public SalesUser save(SalesUser user) {
        return repo.save(user);
    }
}
