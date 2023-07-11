package com.example.accountingassistant.model.menu;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.wpapper.EditMessageTextWrap;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;


public interface MenuActivity {

    String getMenuComand();

    String getDescription();

    List<PartialBotApiMethod> menuRun(User user, Update update);

    default PartialBotApiMethod replaceButton(Update update, User user) {
        if (!update.hasCallbackQuery()) {
            return null;
        }
        val message = update.getCallbackQuery().getMessage();
        val menuName = message.getReplyMarkup().getKeyboard().stream()
                .flatMap(t -> t.stream())
                .filter(e -> e.getCallbackData().equals(update.getCallbackQuery().getData()))
                .findFirst().get().getText();
        return EditMessageTextWrap.init()
                .setChatIdLong(message.getChatId())
                .setMessageId(message.getMessageId())
                .setText("Выбрано меню: " + menuName)
                .build().createMessage();
    }
}
