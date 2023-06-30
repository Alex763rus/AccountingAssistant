package com.example.accountingassistant.model.menu.base;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.wpapper.SendMessageWrap;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.example.accountingassistant.constant.Constant.Command.*;
import static com.example.accountingassistant.constant.Constant.NEW_LINE;

@Component
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
                messageText = getUnregisteredMenuText(user);
                break;
            case EMPLOYEE:
                messageText = getEmployeeMenuText(user);
                break;
            case ADMIN:
                messageText = getAdminMenuText(user);
                break;
        }
        return Arrays.asList(
                SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(EmojiParser.parseToUnicode(messageText))
                        .build().createSendMessage());
    }

    private String getUnregisteredMenuText(User user) {
        val menu = new StringBuilder();
        menu.append("Здравствуйте!").append(NEW_LINE)
                .append("Вас приветствует компания TODO название компании").append(NEW_LINE)
                .append("С помощью данного бота вы можете рассчитать TODO ").append(NEW_LINE)
                .append("Для продолжения работы, укажите, пожалуйста, свои контактные данные, используя меню: ").append(COMMAND_REGISTER).append(NEW_LINE);
        return menu.toString();
    }

    private String getAdminMenuText(User user) {
        val menu = new StringBuilder(getEmployeeMenuText(user));
        menu.append(NEW_LINE)
                .append("Меню администратора: ").append(NEW_LINE)
                .append("- выгрузить новых лидов: ").append(COMMAND_EXPORT_NEW_LEADS).append(NEW_LINE)
                .append("- выгрузить всех лидов: ").append(COMMAND_EXPORT_ALL_LEADS).append(NEW_LINE)
                .append("- выгрузить историю расчетов: ").append(COMMAND_EXPORT_CALCULATION_HISTORY).append(NEW_LINE);
        return menu.toString();
    }

    private String getEmployeeMenuText(User user) {
        val menu = new StringBuilder();
        menu.append("Главное меню:").append(NEW_LINE)
                .append("- справочная информация: ").append(COMMAND_FAQ).append(NEW_LINE)
                .append("- выполнить расчет: ").append(COMMAND_CALCULATION).append(NEW_LINE)
                .append("- наши контакты: ").append(COMMAND_CONTACT).append(NEW_LINE)
                .append("- коммерческое предложение: ").append(COMMAND_OFFER).append(NEW_LINE);
        return menu.toString();
    }

    @Override
    public String getDescription() {
        return " Начало работы";
    }
}
