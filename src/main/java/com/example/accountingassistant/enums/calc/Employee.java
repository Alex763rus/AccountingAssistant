package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum Employee {

    EMPLOYEE_1("0 сотр"),

    EMPLOYEE_2("1 сотр"),

    EMPLOYEE_3("5 сотр"),

    EMPLOYEE_4("10 сотр"),

    EMPLOYEE_5("15 сотр"),

    EMPLOYEE_6("20 сотр"),

    EMPLOYEE_7("25 сотр"),

    EMPLOYEE_8("30 сотр"),

    EMPLOYEE_9("40 сотр"),

    EMPLOYEE_10("50 сотр"),

    EMPLOYEE_11("60 сотр"),

    EMPLOYEE_12("70 сотр"),

    EMPLOYEE_13("80 сотр"),

    EMPLOYEE_14("90 сотр"),

    EMPLOYEE_15("100 сотр"),

    BACK("Назад");


    private String title;

    Employee(String title) {
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
