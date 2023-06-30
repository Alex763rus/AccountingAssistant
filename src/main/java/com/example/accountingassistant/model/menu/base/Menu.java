package com.example.accountingassistant.model.menu.base;

import com.example.accountingassistant.config.BotConfig;
import com.example.accountingassistant.exception.InputCallbackException;
import com.example.accountingassistant.exception.InputLongException;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.MenuActivity;
import com.example.accountingassistant.model.wpapper.SendDocumentWrap;
import com.example.accountingassistant.model.wpapper.SendMessageWrap;
import com.example.accountingassistant.model.wpapper.SendPhotoWrapper;
import com.example.accountingassistant.service.database.UserService;
import com.example.accountingassistant.service.excel.ExcelService;
import com.example.accountingassistant.service.menu.ButtonService;
import com.example.accountingassistant.service.menu.StateService;
import jakarta.persistence.MappedSuperclass;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.example.accountingassistant.constant.Constant.NEW_LINE;
import static javax.swing.text.html.parser.DTDConstants.EMPTY;

@MappedSuperclass
public abstract class Menu implements MenuActivity {

    @Autowired
    protected BotConfig botConfig;

    @Autowired
    protected StateService stateService;

    @Autowired
    protected ExcelService excelService;

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
                .setCaption("TODO тут будут ваши контакты и фото:")
                .setPhoto(new InputFile(new File(botConfig.getInputFilePhotoPath())))
                .build().createMessage();
    }

    protected PartialBotApiMethod getMessageOffer(User user, Update update) {
        return SendDocumentWrap.init()
                .setChatIdLong(user.getChatId())
                .setCaption("TODO тут будет ваше коммерческое предложение:")
                .setDocument(new InputFile(new File(botConfig.getInputFileOfferPath())))
                .build().createMessage();
    }

    protected String getInputCallback(User user, Update update) {
        if (!update.hasCallbackQuery()) {
            throw new InputCallbackException();
        }
        return update.getCallbackQuery().getData();
    }


    protected Integer getInputInteger(User user, Update update) {
        if (!update.hasMessage()) {
            throw new InputLongException("Отсутствует сообщение", 10);
        }
        val message = update.getMessage().getText();
        if (message == null || message.equals(EMPTY) || message.trim().length() > 10 || !checkInteger(message)) {
            throw new InputLongException(message, 10);
        }
        return Integer.parseInt(message);
    }

    protected Long getInputLong(User user, Update update) {
        if (!update.hasMessage()) {
            throw new InputLongException("Отсутствует сообщение", 18);
        }
        val message = update.getMessage().getText();
        if (message == null || message.equals(EMPTY) || message.trim().length() > 18 || !checkInteger(message)) {
            throw new InputLongException(message, 18);
        }
        return Long.parseLong(message);
    }

    private boolean checkInteger(String value) {
        try {
            Long.parseLong(value);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
