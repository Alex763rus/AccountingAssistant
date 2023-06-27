package com.example.accountingassistant.enums.calc;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public enum MoneyTurnover {
    MONEY_TURNOVER_10("от 0 до 10 млн"),
    MONEY_TURNOVER_15("до 15"),
    MONEY_TURNOVER_20("до 20"),
    MONEY_TURNOVER_25("до 25"),
    MONEY_TURNOVER_30("до 30"),
    MONEY_TURNOVER_35("до 35"),
    MONEY_TURNOVER_40("до 40"),
    MONEY_TURNOVER_45("до 45"),
    MONEY_TURNOVER_50("до 50 млн"),

    BACK("Назад");


    private String title;

    MoneyTurnover(String title) {
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
