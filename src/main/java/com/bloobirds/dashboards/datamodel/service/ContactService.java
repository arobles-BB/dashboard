package com.bloobirds.dashboards.datamodel.service;

import com.bloobirds.dashboards.datamodel.Contact;
import com.bloobirds.dashboards.datamodel.repo.ContactRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Stream;

@Service
@Transactional
public class ContactService {
    private final ContactRepository repo;

    public ContactService(ContactRepository repository) {
        this.repo=repository;
    }

    public Stream<Contact> findAll(int page, int pageSize) {
//        return repo.findAllUsersWithAssignTo(PageRequest.of(page,pageSize)).stream();
        return repo.findAll(PageRequest.of(page,pageSize)).stream();
    }

    public Contact save(Contact contact) {
        return repo.save(contact);
    }
}
