package com.example.accountingassistant.model.menu.employee;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendDocumentWrap;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_CONTACT;
import static com.example.accountingassistant.constant.Constant.Command.COMMAND_OFFER;
import static java.util.Objects.requireNonNull;

@Component(COMMAND_OFFER)
@Slf4j
public class MenuOffer extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_OFFER;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        val path = new File(botConfig.getInputFileOfferPath());
        if (path.isDirectory()) {
            val file = requireNonNull(path.listFiles())[0];
            stateService.refreshUser(user);
            return List.of(SendDocumentWrap.init()
                    .setChatIdLong(user.getChatId())
                    .setCaption("Скачайте наше коммерческое предложение")
                    .setDocument(new InputFile(file))
                    .build().createMessage());
        }
        return errorMessageDefault(update);
    }

    @Override
    public String getDescription() {
        return getMenuComand();
    }
}
