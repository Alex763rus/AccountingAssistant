package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum AgencyContract {

    AGENCY_CONTRACT_NO("нет"),
    AGENCY_CONTRACT_10("до 10 арт."),
    AGENCY_CONTRACT_100("11 - 100 арт."),
    AGENCY_CONTRACT_300("101 - 300 арт."),
    AGENCY_CONTRACT_500("301 - 500 арт."),
    AGENCY_CONTRACT_1000("501 - 1000 арт."),

    BACK("Назад");

    private String title;

    AgencyContract(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Map<String, String> getValues() {
        val btns = new LinkedHashMap<String, String>();
        Arrays.stream(values()).forEach(e -> btns.put(e.name(), e.getTitle()));
        return btns;
    }
}
