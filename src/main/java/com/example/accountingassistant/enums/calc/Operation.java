package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Operation {

    OPERATION_100("от 0 до 100"),
    OPERATION_200("от 101 до 200"),
    OPERATION_300("от 201 до 300"),
    OPERATION_400("от 301 до 400"),
    OPERATION_500("от 401 до 500"),
    OPERATION_800("от 501 до 800"),
    OPERATION_1000("от 801 до 1000"),
    OPERATION_1300("от 1001 до 1300"),
    OPERATION_1500("от 1301 до 1500"),
    OPERATION_1800("от 1501 до 1800"),
    OPERATION_2000("от 1801 до 2000"),
    OPERATION_2300("от 2001 до 2300"),
    OPERATION_2500("от 2301 до 2500"),
    OPERATION_3000("от 2501 до 3000"),
    OPERATION_4000("от 3001 до 4000"),

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
