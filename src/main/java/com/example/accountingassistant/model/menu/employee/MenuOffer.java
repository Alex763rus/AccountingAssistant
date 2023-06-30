package com.example.accountingassistant.model.menu.employee;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_CONTACT;
import static com.example.accountingassistant.constant.Constant.Command.COMMAND_OFFER;

@Component
@Slf4j
public class MenuOffer extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_OFFER;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        stateService.refreshUser(user);
        return List.of(getMessageOffer(user, update));
    }

    @Override
    public String getDescription() {
        return getMenuComand();
    }
}
