package com.bloobirds.dashboards.datamodel.service;

import com.bloobirds.dashboards.datamodel.Company;
import com.bloobirds.dashboards.datamodel.repo.CompanyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Stream;

@Service
@Transactional // to avoid LazyInitializationException in attributes class
public class CompanyService {

    private final CompanyRepository repo;

    public CompanyService(CompanyRepository repository) {
        this.repo=repository;
    }

    public Stream<Company> findAll(int page, int pageSize) {
        return repo.findAll(PageRequest.of(page,pageSize)).stream();
    }
}
