package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum CashBox {

    DOCUMENT_NO("Нет"),
    DOCUMENT_MATCHING_1("1 касса"),
    DOCUMENT_MATCHING_2("2 кассы"),
    DOCUMENT_MATCHING_3("3 и более"),

    BACK("Назад");

    private String title;

    CashBox(String title) {
        this.title = title;
    }
    public static Map<String, String> getValues(){
        val btns = new LinkedHashMap<String, String>();
        Arrays.stream(values()).forEach(e -> btns.put(e.name(), e.getTitle()));
        return btns;
    }
    public String getTitle() {
        return title;
    }
}
