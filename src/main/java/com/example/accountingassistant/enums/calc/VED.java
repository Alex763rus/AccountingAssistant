package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum VED {

    VED_NO("нет"),
    VED_10("до 10 операций"),
    VED_30("от 11 до 30 операций"),

    BACK("Назад");

    private String title;

    VED(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Map<String, String> getValues(){
        val btns = new LinkedHashMap<String, String>();
        Arrays.stream(values()).forEach(e -> btns.put(e.name(), e.getTitle()));
        return btns;
    }
}
