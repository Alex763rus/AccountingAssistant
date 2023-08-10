package com.example.accountingassistant.config;

import com.example.accountingassistant.enums.UserRole;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.accountingassistant.constant.Constant.Command.*;
import static com.example.accountingassistant.constant.Constant.ConfigParams.*;
import static com.example.accountingassistant.enums.UserRole.*;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_DEFAULT;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_START;
import static org.example.tgcommons.constant.Constant.ConfigParams.PROPERTY_SOURCE;
import static org.example.tgcommons.constant.Constant.ConfigParams.BOT_VERSION;
import static org.example.tgcommons.constant.Constant.ConfigParams.BOT_USERNAME;
import static org.example.tgcommons.constant.Constant.ConfigParams.BOT_TOKEN;
import static org.example.tgcommons.constant.Constant.ConfigParams.ADMIN_CHAT_ID;

@Configuration
@Getter
@PropertySource(PROPERTY_SOURCE)
public class BotConfig {

    @Value(BOT_VERSION)
    String botVersion;

    @Value(BOT_USERNAME)
    String botUserName;

    @Value(BOT_TOKEN)
    String botToken;

    @Value(ADMIN_CHAT_ID)
    String adminChatId;

    @Value(INPUT_FILE_PATH)
    String inputFilePath;

    @Value(INPUT_FILE_PHOTO_PATH)
    String inputFilePhotoPath;

    @Value(INPUT_FILE_OFFER_PATH)
    String inputFileOfferPath;

    @Bean
    public Map<UserRole, List<String>> roleAccess() {
        val roleAccess = new HashMap<UserRole, List<String>>();
        roleAccess.put(BLOCKED, List.of(COMMAND_DEFAULT, COMMAND_START));
        roleAccess.put(UNREGISTERED, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_REGISTER));
        roleAccess.put(EMPLOYEE, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_FAQ, COMMAND_CALCULATION, COMMAND_CONTACT, COMMAND_OFFER));
        roleAccess.put(ADMIN, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_FAQ, COMMAND_CALCULATION, COMMAND_CONTACT, COMMAND_OFFER, COMMAND_EXPORT_NEW_LEADS, COMMAND_EXPORT_ALL_LEADS, COMMAND_EXPORT_CALCULATION_HISTORY));
        return roleAccess;
    }

}
