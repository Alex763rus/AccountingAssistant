package com.example.accountingassistant.model.menu.base;

import com.example.accountingassistant.model.jpa.User;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.accountingassistant.constant.Constant.Command.*;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_START;
import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;

@Component(COMMAND_START)
@Slf4j
public class MenuStart extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_START;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        String messageText = "";
        switch (user.getUserRole()) {
            case BLOCKED:
                messageText = "Доступ запрещен";
                break;
            case UNREGISTERED:
                return getUnregisteredMenuText(user, update);
            case EMPLOYEE:
                messageText = getEmployeeMenuText();
                break;
            case ADMIN:
                messageText = getAdminMenuText(user);
                break;
        }
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(EmojiParser.parseToUnicode(messageText))
                .build().createMessageList();
    }

    private List<PartialBotApiMethod> getUnregisteredMenuText(User user, Update update) {
        val answer = new ArrayList<PartialBotApiMethod>();
        answer.add(getMessageContact(user, update));
        String messageText = "Для продолжения работы, укажите, пожалуйста, свои контактные данные, используя меню: " +
                COMMAND_REGISTER + NEW_LINE;
        answer.add(SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(EmojiParser.parseToUnicode(messageText))
                .build().createMessage());
        return answer;
    }

    private String getAdminMenuText(User user) {
        val menu = new StringBuilder(getEmployeeMenuText());
        menu.append(NEW_LINE)
                .append("Меню администратора: ").append(NEW_LINE)
                .append("- выгрузить новых лидов: ").append(COMMAND_EXPORT_NEW_LEADS).append(NEW_LINE)
                .append("- выгрузить всех лидов: ").append(COMMAND_EXPORT_ALL_LEADS).append(NEW_LINE)
                .append("- выгрузить историю расчетов: ").append(COMMAND_EXPORT_CALCULATION_HISTORY).append(NEW_LINE);
        return menu.toString();
    }

    private String getEmployeeMenuText() {
        val menu = new StringBuilder();
        menu.append("Главное меню:").append(NEW_LINE)
                .append("- выполнить расчет: ").append(COMMAND_CALCULATION).append(NEW_LINE)
                .append("- справочная информация: ").append(COMMAND_FAQ).append(NEW_LINE)
                .append("- наши контакты: ").append(COMMAND_CONTACT).append(NEW_LINE)
                .append("- коммерческое предложение: ").append(COMMAND_OFFER).append(NEW_LINE);
        return menu.toString();
    }

    @Override
    public String getDescription() {
        return " Начало работы";
    }
}
