package com.example.accountingassistant.model.menu.base;

import com.example.accountingassistant.config.BotConfig;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.MenuActivity;
import com.example.accountingassistant.model.wpapper.SendMessageWrap;
import com.example.accountingassistant.model.wpapper.SendPhotoWrapper;
import com.example.accountingassistant.service.database.UserService;
import com.example.accountingassistant.service.excel.ExcelGenerateService;
import com.example.accountingassistant.service.excel.FileUploadService;
import com.example.accountingassistant.service.menu.ButtonService;
import com.example.accountingassistant.service.menu.StateService;
import jakarta.persistence.MappedSuperclass;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@MappedSuperclass
public abstract class Menu implements MenuActivity {

    @Autowired
    protected BotConfig botConfig;

    @Autowired
    protected StateService stateService;

    @Autowired
    protected FileUploadService fileUploadService;

    @Autowired
    protected ExcelGenerateService excelGenerateService;

    @Autowired
    protected ButtonService buttonService;

    @Autowired
    protected UserService userService;

    private static final String DEFAULT_TEXT_ERROR = "Ошибка! Команда не найдена";

    protected List<PartialBotApiMethod> errorMessageDefault(Update update) {
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(DEFAULT_TEXT_ERROR)
                .build().createSendMessage());
    }

    protected List<PartialBotApiMethod> errorMessage(Update update, String message) {
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(message)
                .build().createSendMessage());
    }

    protected PartialBotApiMethod createAdminMessage(String message) {
        return SendMessageWrap.init()
                .setChatIdString(botConfig.getAdminChatId())
                .setText(message)
                .build().createSendMessage();
    }

    protected PartialBotApiMethod getMessageContact(User user, Update update) {
        return SendPhotoWrapper.init()
                .setChatIdLong(user.getChatId())
                .setCaption("TODO тут будут ваши контакты и фото")
                .setPhoto(new InputFile(new File(botConfig.getInputFilePhotoPath())))
                .build().createMessage();
    }

}
