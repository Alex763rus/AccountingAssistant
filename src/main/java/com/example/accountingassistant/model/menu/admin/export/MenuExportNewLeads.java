package com.example.accountingassistant.model.menu.admin.export;

import com.example.accountingassistant.enums.LeadExportStatus;
import com.example.accountingassistant.model.jpa.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_EXPORT_NEW_LEADS;
import static com.example.accountingassistant.enums.LeadExportStatus.EXPORTED_LEAD;
import static com.example.accountingassistant.enums.LeadExportStatus.NEW_LEAD;

@Component(COMMAND_EXPORT_NEW_LEADS)
@Slf4j
public class MenuExportNewLeads extends MenuExportLeadsBase {

    @Override
    public String getMenuComand() {
        return COMMAND_EXPORT_NEW_LEADS;
    }

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            switch (stateService.getState(user)) {
                case FREE:
                    return exportNewLeadsToExcel(user, update);
            }
            return errorMessageDefault(update);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> exportNewLeadsToExcel(User user, Update update) {
        val userList = userService.findUserByLeadExportStatus(NEW_LEAD);
        val answer = exportLeadToExcel(user, userList);
        userList.forEach(e -> e.setLeadExportStatus(EXPORTED_LEAD));
        userService.saveUser(userList);
        return answer;
    }

    @Override
    public String getDescription() {
        return "Новые лиды";
    }
}
