package com.example.accountingassistant.model.jpa;

import com.example.accountingassistant.enums.calc.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "calculation")
public class Calculation {

    @Id
    @Column(name = "calculation_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calculationId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "form")
    private Form form;

    @Column(name = "mode")
    private Mode mode;

    @Column(name = "employee")
    private Employee employee;

    @Column(name = "money_turnover")
    private MoneyTurnover moneyTurnover;

    @Column(name = "operation")
    private Operation operation;

    @Column(name = "nds_agent")
    private Integer ndsAgent;

    @Column(name = "ndfl_agent")
    private Integer ndflAgent;

    @Column(name = "agency_contract")
    private AgencyContract agencyContract;

    @Column(name = "ved")
    private VED ved;

    @Column(name = "detached")
    private Integer detached;

    @Column(name = "document_matching")
    private DocumentMatching documentMatching;

    @Column(name = "result_standart")
    private Integer resultStandart;

    @Column(name = "result_expert")
    private Integer resultExpert;

    @Column(name = "calculatio_date")
    private Timestamp calculationDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calculation that = (Calculation) o;
        return Objects.equals(calculationId, that.calculationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calculationId);
    }
}
