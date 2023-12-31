package com.example.accountingassistant.model.menu.unregister;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_REGISTER;
import static com.example.accountingassistant.enums.State.*;
import static com.example.accountingassistant.enums.UserRole.EMPLOYEE;

@Component(COMMAND_REGISTER)
@Slf4j
public class MenuRegister extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_REGISTER;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            switch (stateService.getState(user)) {
                case FREE:
                    return freelogic(user);
                case REGISTER_WAIT_FIO:
                    return registerWaitFioLogic(user, update);
                case REGISTER_WAIT_PHONE:
                    return registerWaitPhoneLogic(user, update);
                default:
                    return errorMessageDefault(update);
            }
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> registerWaitPhoneLogic(User user, Update update) {
        val phone = getInputLong(user, update);
        user.setPhone(phone.toString());
        user.setUserRole(EMPLOYEE);
        userService.saveUser(user);
        stateService.deleteUser(user);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText("Контактные данные успешно сохранены, бот готов к работе!")
                .build().createMessageList();
    }

    private List<PartialBotApiMethod> registerWaitFioLogic(User user, Update update) {
        if (!update.hasMessage()) {
            return errorMessage(update, "Сообщение должно содержать ФИО");
        }
        user.setFio(update.getMessage().getText());
        userService.saveUser(user);
        stateService.setState(user, REGISTER_WAIT_PHONE);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText("Шаг 2/2 Укажите ваш телефон, используя только цифры, (пример 88007777777):")
                .build().createMessageList();
    }

    private List<PartialBotApiMethod> freelogic(User user) throws ParseException, IOException {
        stateService.setState(user, REGISTER_WAIT_FIO);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText("Шаг 1/2 Укажите ваше ФИО:")
                .build().createMessageList();
    }


    @Override
    public String getDescription() {
        return getMenuComand();
    }

}
