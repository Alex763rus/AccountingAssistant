package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Operation {

    OPERATION_100("0 - 100"),
    OPERATION_200("101 - 200"),
    OPERATION_300("201 - 300"),
    OPERATION_400("301 - 400"),
    OPERATION_500("401 - 500"),
    OPERATION_800("501 - 800"),
    OPERATION_1000("801 - 1000"),
    OPERATION_1300("1001 - 1300"),
    OPERATION_1500("1301 - 1500"),
    OPERATION_1800("1501 - 1800"),
    OPERATION_2000("1801 - 2000"),
    OPERATION_2300("2001 - 2300"),
    OPERATION_2500("2301 - 2500"),
    OPERATION_3000("2501 - 3000"),
    OPERATION_4000("3001 - 4000"),

    BACK("Назад");

    private String title;

    Operation(String title) {
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
