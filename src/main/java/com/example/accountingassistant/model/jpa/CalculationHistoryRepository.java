package com.example.accountingassistant.model.jpa;

import org.springframework.data.repository.CrudRepository;

public interface CalculationHistoryRepository extends CrudRepository<Calculation, Long> {

}
