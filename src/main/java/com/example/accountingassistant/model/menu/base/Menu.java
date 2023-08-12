package com.example.accountingassistant.model.menu.base;

import com.example.accountingassistant.config.BotConfig;
import com.example.accountingassistant.exception.InputCallbackException;
import com.example.accountingassistant.exception.InputLongException;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.MenuActivity;
import com.example.accountingassistant.service.database.UserService;
import com.example.accountingassistant.service.excel.ExcelService;
import com.example.accountingassistant.service.menu.StateService;
import jakarta.persistence.MappedSuperclass;
import lombok.val;
import org.example.tgcommons.model.button.Button;
import org.example.tgcommons.model.button.ButtonsDescription;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.example.tgcommons.model.wrapper.SendPhotoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.tgcommons.constant.Constant.TextConstants.EMPTY;
import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;
import static org.example.tgcommons.utils.ButtonUtils.createVerticalColumnMenu;

@MappedSuperclass
public abstract class Menu implements MenuActivity {

    @Autowired
    protected BotConfig botConfig;

    @Autowired
    protected StateService stateService;

    @Autowired
    protected ExcelService excelService;

    @Autowired
    protected UserService userService;

    private static final String DEFAULT_TEXT_ERROR = "Ошибка! Команда не найдена";

    protected List<PartialBotApiMethod> errorMessageDefault(Update update) {
        return SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(DEFAULT_TEXT_ERROR)
                .build().createMessageList();
    }

    protected List<PartialBotApiMethod> errorMessage(Update update, String message) {
        return SendMessageWrap.init()
                .setChatIdLong(update.getMessage().getChatId())
                .setText(message)
                .build().createMessageList();
    }

    protected PartialBotApiMethod createAdminMessage(String message) {
        return SendMessageWrap.init()
                .setChatIdString(botConfig.getAdminChatId())
                .setText(message)
                .build().createMessage();
    }

    protected PartialBotApiMethod getMessageContact(User user, Update update) {
        val contactText = new StringBuilder();
        contactText.append("Добрый день!").append(NEW_LINE)
                .append("Я - Людмила, руководитель компании Дечизо Главбух.").append(NEW_LINE)
                .append("Я предприниматель.").append(NEW_LINE)
                .append("Я такая же, как вы, и не хочу  платить налоги.").append(NEW_LINE)
                .append("Но, как руководитель бухгалтерской компании, я знаю, как правильно их платить.").append(NEW_LINE)
                .append(NEW_LINE)
                .append("Свяжитесь удобным для вас способом:").append(NEW_LINE)
                .append("позвонить: +79037995128").append(NEW_LINE)
                .append("Написать в чат:").append(NEW_LINE);

        val btns = ButtonsDescription.init()
                .setCountColumn(2)
                .setButtons(new ArrayList<>(List.of(
                        Button.init().setKey("key1").setValue("WhatsApp").setLink("https://wa.me/79037995128/").build(),
                        Button.init().setKey("key2").setValue("Telegram").setLink("t.me/glavbuh_lchur/").build(),
                        Button.init().setKey("key3").setValue("Подпишитесь на наш канал").setLink("t.me/usnkalmykia/").build()
                )))
                .build();

        return SendPhotoWrapper.init()
                .setChatIdLong(user.getChatId())
                .setCaption(contactText.toString())
                .setPhoto(new InputFile(new File(botConfig.getInputFilePhotoPath())))
                .setInlineKeyboardMarkup(createVerticalColumnMenu(btns))
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
