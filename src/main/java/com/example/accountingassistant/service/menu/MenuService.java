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
import com.example.accountingassistant.model.wpapper.EditMessageTextWrap;
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

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_START;
import static com.example.accountingassistant.enums.State.FREE;

@Slf4j
@Service
public class MenuService {


    @Autowired
    private MenuDefault menuActivityDefault;

    @Autowired
    private StateService stateService;

    @Autowired
    private MenuExportCalculationHistory menuExportCalculationHistory;

    @Autowired
    private UserService userService;

    @Autowired
    private Security security;

    @Autowired
    private MenuFaq menuFaq;

    @Autowired
    private MenuCalculation menuCalculation;

    @Autowired
    private MenuStart menuStart;

    @Autowired
    private MenuContact menuContact;

    @Autowired
    private MenuOffer menuOffer;

    @Autowired
    private MenuExportAllLeads menuExportAllLeads;


    @Autowired
    private MenuExportNewLeads menuExportNewLeads;


    @Autowired
    private MenuRegister menuRegister;


    @PostConstruct
    public void init() {
        // Список всех возможных обработчиков меню:
        security.setMainMenu(List.of(menuStart, menuFaq, menuExportCalculationHistory, menuContact, menuCalculation, menuRegister, menuOffer, menuExportAllLeads, menuExportNewLeads));
    }

    public List<PartialBotApiMethod> messageProcess(Update update) {
        val user = userService.getUser(update);
        MenuActivity menuActivity = null;
        if (update.hasMessage()) {
            for (val menu : security.getMainMenu()) {
                if (menu.getMenuComand().equals(update.getMessage().getText())) {
                    if (security.checkAccess(user, menu.getMenuComand())) {
                        menuActivity = menu;
                    } else {
                        menuActivity = menuActivityDefault;
                    }
                }
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
        if (update.hasCallbackQuery()) {
            val message = update.getCallbackQuery().getMessage();
            val menuName = message.getReplyMarkup().getKeyboard().stream()
                    .flatMap(t -> t.stream())
                    .filter(e -> e.getCallbackData().equals(update.getCallbackQuery().getData()))
                    .findFirst().get().getText();
            answer.add(EditMessageTextWrap.init()
                    .setChatIdLong(message.getChatId())
                    .setMessageId(message.getMessageId())
                    .setText("Выбрано меню: " + menuName)
                    .build().createMessage());
        }
        answer.addAll(menuActivity.menuRun(user, update));
        if (stateService.getState(user) == FREE && !menuActivity.getMenuComand().equals(menuStart.getMenuComand())) {
            answer.addAll(menuStart.menuRun(user, update));
        }
        return answer;
    }

    public List<BotCommand> getMainMenuComands() {
        val listofCommands = new ArrayList<BotCommand>();
        security.getMainMenu().stream()
                .filter(e -> e.getMenuComand().equals(COMMAND_START))
                .forEach(e -> listofCommands.add(new BotCommand(e.getMenuComand(), e.getDescription())));
        return listofCommands;
    }

    private String getChatId(Update update) {
        if (update.hasMessage()) {
            return String.valueOf(update.getMessage().getChatId());
        }
        if (update.hasCallbackQuery()) {
            return String.valueOf(update.getCallbackQuery().getMessage().getChatId());
        }
        return null;
    }
}