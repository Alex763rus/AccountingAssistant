package com.example.accountingassistant.model.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FaqRepository extends CrudRepository<Faq, Long> {

    @Override
    public List<Faq> findAll();

}
