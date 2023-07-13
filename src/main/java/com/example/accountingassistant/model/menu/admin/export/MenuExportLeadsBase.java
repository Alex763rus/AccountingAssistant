package com.example.accountingassistant.model.menu.admin.export;

import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import lombok.val;
import org.example.tgcommons.model.wrapper.SendDocumentWrap;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MenuExportLeadsBase extends Menu {

    protected List<PartialBotApiMethod> exportLeadToExcel(User user, List<User> exportedUser) {
        List<List<String>> excelData = new ArrayList<>();
        excelData.add(Arrays.asList("№", "Чат ИД:", "Логин:", "ФИО:", "Телефон:", "Фамилия ТГ:", "Имя ТГ:", "Дата регистрации:"));
        for (int i = 0; i < exportedUser.size(); ++i) {
            val userBd = exportedUser.get(i);
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
}
