package com.example.accountingassistant.service.menu;

import com.example.accountingassistant.model.dictionary.security.Security;
import com.example.accountingassistant.model.menu.*;
import com.example.accountingassistant.model.menu.admin.export.MenuExportAllLeads;
import com.example.accountingassistant.model.menu.admin.export.MenuExportCalculationHistory;
import com.example.accountingassistant.model.menu.admin.export.MenuExportNewLeads;
import com.example.accountingassistant.model.menu.base.MenuDefault;
import com.example.accountingassistant.model.menu.base.MenuStart;
import com.example.accountingassistant.model.menu.employee.MenuCalculation;
import com.example.accountingassistant.model.menu.employee.MenuContact;
import com.example.accountingassistant.model.menu.employee.MenuFaq;
import com.example.accountingassistant.model.menu.employee.MenuOffer;
import com.example.accountingassistant.model.menu.unregister.MenuRegister;
import com.example.accountingassistant.service.database.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

import static com.example.accountingassistant.enums.State.FREE;
import static org.example.tgcommons.constant.Constant.Command.COMMAND_START;

@Slf4j
@Service
public class MenuService {

    @Autowired
    private UserService userService;

    @Autowired
    private Security security;

    @Autowired
    private MenuDefault menuActivityDefault;

    @Autowired
    private StateService stateService;

    @Autowired
    private MenuStart menuStart;

    public List<PartialBotApiMethod> messageProcess(Update update) {
        val user = userService.getUser(update);
        MenuActivity menuActivity = null;
        if (update.hasMessage()) {
            val menu = security.getMenuActivity(update.getMessage().getText());
            if (menu != null) {
                menuActivity = security.checkAccess(user, menu.getMenuComand()) ? menu : menuActivityDefault;
            }
        }
        if (menuActivity != null) {
            stateService.setMenu(user, menuActivity);
        } else {
            menuActivity = stateService.getMenu(user);
            if (menuActivity == null) {
                log.warn("Не найдена команда с именем: " + update.getMessage().getText());
                menuActivity = menuActivityDefault;
            }
        }

        val answer = new ArrayList<PartialBotApiMethod>();
        val editButton = menuActivity.replaceButton(update, user);
        if (editButton != null) {
            answer.add(editButton);
        }
        answer.addAll(menuActivity.menuRun(user, update));
        if (stateService.getState(user) == FREE && !menuActivity.getMenuComand().equals(menuStart.getMenuComand())) {
            answer.addAll(menuStart.menuRun(user, update));
        }
        return answer;
    }

    public List<BotCommand> getMainMenuComands() {
        val menu = security.getMenuActivity(COMMAND_START);
        return List.of(new BotCommand(menu.getMenuComand(), menu.getDescription()));
    }

}