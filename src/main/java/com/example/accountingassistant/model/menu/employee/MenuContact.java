package com.example.accountingassistant.model.menu.employee;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import com.example.accountingassistant.model.wpapper.SendPhotoWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_CONTACT;

@Component
@Slf4j
public class MenuContact extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_CONTACT;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        return List.of(getMessageContact(user, update));
    }

    @Override
    public String getDescription() {
        return getMenuComand();
    }
}
