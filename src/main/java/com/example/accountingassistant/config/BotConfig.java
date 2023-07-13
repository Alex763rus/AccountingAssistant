package com.example.accountingassistant.config;

import com.example.accountingassistant.enums.UserRole;
import com.example.accountingassistant.model.dictionary.security.Security;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.List;

import static com.example.accountingassistant.constant.Constant.*;
import static com.example.accountingassistant.constant.Constant.Command.*;
import static com.example.accountingassistant.enums.UserRole.*;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_DEFAULT;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_START;
import static org.example.tgcommons.constant.Constant.TextConstants.SHIELD;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.version}")
    String botVersion;

    @Value("${bot.username}")
    String botUserName;

    @Value("${bot.token}")
    String botToken;

    @Value("${admin.chatid}")
    String adminChatId;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${input.file.path}")
    String inputFilePath;

    @Value("${input.file.photo.path}")
    String inputFilePhotoPath;

    @Value("${input.file.offer.path}")
    String inputFileOfferPath;

    private String getCurrentPath() {
        return System.getProperty(USER_DIR) + SHIELD;
    }


    @Bean
    public Security security() {
        val roleSecurity = new Security();

        // Настройка команд по ролям:
        val roleAccess = new HashMap<UserRole, List<String>>();
        roleAccess.put(BLOCKED, List.of(COMMAND_DEFAULT, COMMAND_START));
        roleAccess.put(UNREGISTERED, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_REGISTER));
        roleAccess.put(EMPLOYEE, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_FAQ, COMMAND_CALCULATION, COMMAND_CONTACT, COMMAND_OFFER));
        roleAccess.put(ADMIN, List.of(COMMAND_DEFAULT, COMMAND_START, COMMAND_FAQ, COMMAND_CALCULATION, COMMAND_CONTACT, COMMAND_OFFER, COMMAND_EXPORT_NEW_LEADS, COMMAND_EXPORT_ALL_LEADS, COMMAND_EXPORT_CALCULATION_HISTORY));
        roleSecurity.setRoleAccess(roleAccess);

        return roleSecurity;
    }

}
