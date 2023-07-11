package com.example.accountingassistant.model.menu.admin.export;

import com.example.accountingassistant.model.jpa.*;
import com.example.accountingassistant.model.menu.base.Menu;
import com.example.accountingassistant.model.wpapper.SendDocumentWrap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

import static com.example.accountingassistant.constant.Constant.Command.*;

@Component
@Slf4j
public class MenuExportCalculationHistory extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_EXPORT_CALCULATION_HISTORY;
    }

    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            switch (stateService.getState(user)) {
                case FREE:
                    return exportCalculationHistoryToExcel(user, update);
            }
            return errorMessageDefault(update);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> exportCalculationHistoryToExcel(User user, Update update) {
        val calculationList = calculationHistoryRepository.findAll();
        List<List<String>> excelData = new ArrayList<>();
        excelData.add(Arrays.asList("№", "ИД:", "Логин:", "ФИО:", "Телефон:", "Форма:", "Режим:", "Сотрудники:", "Оборот, млн.:"
                , "Операции:", "Агент по НДС:", "Агент по НДФЛ:", "Агентский дог:", "ВЭД:", "Обособленное:", "Сопоставление документов:", "Стандарт:", "Эксперт:", "Дата расчета:"));
        for (int i = 0; i < calculationList.size(); ++i) {
            val calculation = calculationList.get(i);
            excelData.add(
                    Arrays.asList(
                            String.valueOf(i + 1)
                            , String.valueOf(calculation.getCalculationId())
                            , calculation.getUser().getUserName()
                            , calculation.getUser().getFio()
                            , calculation.getUser().getPhone()

                            , calculation.getForm().getTitle()
                            , calculation.getMode().getTitle()
                            , calculation.getEmployee().getTitle()
                            , calculation.getMoneyTurnover().getTitle()
                            , calculation.getOperation().getTitle()
                            , String.valueOf(calculation.getNdsAgent())
                            , String.valueOf(calculation.getNdflAgent())
                            , calculation.getAgencyContract().getTitle()
                            , calculation.getVed().getTitle()
                            , String.valueOf(calculation.getDetached())
                            , calculation.getCashBox().getTitle()
                            , String.valueOf(Optional.ofNullable(calculation.getResultStandart()).orElse(0))
                            , String.valueOf(Optional.ofNullable(calculation.getResultExpert()).orElse(0))

                            , String.valueOf(calculation.getCalculationDate())
                    )
            );
        }
        stateService.refreshUser(user);
        return Arrays.asList(
                SendDocumentWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setDocument(excelService.createExcelDocument("Расчеты", excelData))
                        .setCaption(getDescription())
                        .build().createMessage());
    }

    @Override
    public String getDescription() {
        return "История рсчетов";
    }
}
