package com.example.accountingassistant.model.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface CalculationHistoryRepository extends CrudRepository<CalculationHistory, Long> {

}
