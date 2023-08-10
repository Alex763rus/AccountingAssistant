package com.example.accountingassistant.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Constant {
    @NoArgsConstructor(access = PRIVATE)
    public static final class Command {

        public static final String COMMAND_REGISTER = "/register";

        public static final String COMMAND_FAQ = "/faq";
        public static final String COMMAND_CALCULATION = "/calculation";
        public static final String COMMAND_CONTACT = "/contact";
        public static final String COMMAND_OFFER = "/offer";

        public static final String COMMAND_EXPORT_NEW_LEADS = "/newleads";
        public static final String COMMAND_EXPORT_ALL_LEADS = "/allleads";
        public static final String COMMAND_EXPORT_CALCULATION_HISTORY = "/history";

    }

    @NoArgsConstructor(access = PRIVATE)
    public static final class ConfigParams {
        public static final String INPUT_FILE_PATH = "${input.file.path}";
        public static final String INPUT_FILE_PHOTO_PATH = "${input.file.photo.path}";
        public static final String INPUT_FILE_OFFER_PATH = "${input.file.offer.path}";

    }
    public static final String APP_NAME = "accountingassistant";
    public static final String BACK = "Назад";


}
