package com.example.accountingassistant.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constant {
    @NoArgsConstructor(access = PRIVATE)
    public final class App {

    }

    @NoArgsConstructor(access = PRIVATE)
    public final class Command {

        public static final String COMMAND_REGISTER = "/register";

        public static final String COMMAND_FAQ = "/faq";
        public static final String COMMAND_CALCULATION = "/calculation";
        public static final String COMMAND_CONTACT = "/contact";
        public static final String COMMAND_OFFER = "/offer";

        public static final String COMMAND_EXPORT_NEW_LEADS = "/newleads";
        public static final String COMMAND_EXPORT_ALL_LEADS = "/allleads";
        public static final String COMMAND_EXPORT_CALCULATION_HISTORY = "/history";

    }

    public static String APP_NAME = "accountingassistant";
    public static String BACK = "Назад";
    public static String USER_DIR = "user.dir";

}
