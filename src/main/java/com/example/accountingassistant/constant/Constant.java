package com.example.accountingassistant.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constant {
    @NoArgsConstructor(access = PRIVATE)
    public final class  App{

    }

    @NoArgsConstructor(access = PRIVATE)
    public final class FileOutputName {
        public static final String FILE_NAME_LENTA = "Лента";

        public static final String FILE_NAME_COFIX = "Рулог Кофикс";

        public static final String FILE_NAME_BOGORODSK = "Х5 Богородск";

        public static final String FILE_NAME_SAMOKAT = "Самокат";
        public static final String FILE_NAME_DOMINOS = "Рулог Доминос";
    }

    @NoArgsConstructor(access = PRIVATE)
    public final class Company {
        public static final String COMPANY_NOT_FOUND = "Компания отсутствует";
        public static final String COMPANY_ADVANTUM = "Адвантум";
        public static final String COMPANY_NAME_LENTA = "Лента";
        public static final String COMPANY_NAME_BUSH_AVTOPROM = "Буш-Автопром";
    }
    @NoArgsConstructor(access = PRIVATE)
    public final class Command {

        public static final String COMMAND_DEFAULT = "/default";
        public static final String COMMAND_START = "/start";

        public static final String COMMAND_FAQ = "/faq";
        public static final String COMMAND_CALCULATION = "/calculation";
        public static final String COMMAND_CONTACT = "/contact";
        public static final String COMMAND_GET_KP = "/getkp";

        public static final String COMMAND_NEW_LEADS = "/newleads";
        public static final String COMMAND_ALL_LEADS = "/allleads";
        public static final String COMMAND_CALCULATION_HISTORY = "/history";

    }



    public static String APP_NAME = "accountingassistant";
    public static String PARSE_MODE = "Markdown";

    public static String USER_DIR = "user.dir";
    public static String WHITE_LIST_FILE_NAME = "WhiteListUsers.json";
    public static String SENDER_SETTING_FILE_NAME = "SenderSettings.json";
    public static final String SHIELD = "\\";
    public static final String EMPTY = "";

    public static final String STAR = "*";

    public static String NEW_LINE = "\n";
    public static String SPACE = " ";
    public static String SHEET_RESULT_NAME = "ИМПОРТ";



}
