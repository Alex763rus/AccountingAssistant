package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Mode {

    USN_6_PATENT("УСН 6%, патент"),
    USN_15_USN_6_COMBINATION("УСН 15%, УСН 6%+совмещение"),
    BASIC("ОСНО"),
    USN_6_COMBINATION("УСН 15% + совмещение"),
    BASIC_COMBINATION("ОСНО+совмещение"),

    BACK("Назад");

    private String title;

    Mode(String title) {
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
