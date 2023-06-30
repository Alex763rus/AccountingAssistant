package com.example.accountingassistant.model.menu.admin.export;

import com.example.accountingassistant.model.jpa.CalculationHistoryRepository;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.jpa.UserRepository;
import com.example.accountingassistant.model.menu.base.Menu;
import com.example.accountingassistant.model.wpapper.SendDocumentWrap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_EXPORT_ALL_LEADS;

@Component
@Slf4j
public class MenuExportAllLeads extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_EXPORT_ALL_LEADS;
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            switch (stateService.getState(user)) {
                case FREE:
                    return exportAllLeadsToExcel(user, update);
            }
            return errorMessageDefault(update);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> exportAllLeadsToExcel(User user, Update update) {
        val userList = userRepository.findAll();
        List<List<String>> excelData = new ArrayList<>();
        excelData.add(Arrays.asList("№", "Чат ИД:", "Логин:", "ФИО:", "Телефон:", "Фамилия ТГ:", "Имя ТГ:", "Дата регистрации:"));
        for (int i = 0; i < userList.size(); ++i) {
            val userBd = userList.get(i);
            excelData.add(
                    Arrays.asList(
                            String.valueOf(i + 1)
                            , String.valueOf(userBd.getChatId())
                            , userBd.getUserName()
                            , userBd.getFio()
                            , userBd.getPhone()
                            , userBd.getFirstName()
                            , userBd.getLastName()
                            , String.valueOf(userBd.getRegisteredAt())
                    )
            );
        }
        stateService.refreshUser(user);
        return Arrays.asList(
                SendDocumentWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setDocument(excelService.createExcelDocument("Все лиды", excelData))
                        .setCaption(getDescription())
                        .build().createMessage());
    }

    @Override
    public String getDescription() {
        return "Все лиды";
    }
}
