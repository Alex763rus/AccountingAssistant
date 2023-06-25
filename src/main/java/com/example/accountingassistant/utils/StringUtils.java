package com.example.accountingassistant.utils;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.example.accountingassistant.constant.Constant.SHIELD;

@Getter
@SuperBuilder(setterPrefix = "set", builderMethodName = "init", toBuilder = true)
public class StringUtils {

    private static final String[] shieldingSimbols = {"_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", " -", "=", "|", "{", "}", ".", "!"};

    public static String prepareShield(String source) {
        for (String shieldingSimbol : shieldingSimbols) {
            source = source.replace(shieldingSimbol, SHIELD + shieldingSimbol);
        }
        return source;
    }

    public static String prepareTaskId(Long taskId){
        return String.format("%05d", taskId);
    }

}
