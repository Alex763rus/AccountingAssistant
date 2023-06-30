package com.example.accountingassistant.model.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CalculationHistoryRepository extends CrudRepository<Calculation, Long> {

    @Override
    public List<Calculation> findAll();

}
