package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum DocumentMatching {

    DOCUMENT_MATCHING_0("нет"),
    DOCUMENT_MATCHING_200("до 200 позиций"),
    DOCUMENT_MATCHING_500("от 201 до 500 позиций"),
    DOCUMENT_MATCHING_1000("от 501 до 1000 позиций"),
    DOCUMENT_MATCHING_5000("от 2001 до 5000 позиций"),
    DOCUMENT_MATCHING_5001("от 5001 и выше"),

    BACK("Назад");

    private String title;

    DocumentMatching(String title) {
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
