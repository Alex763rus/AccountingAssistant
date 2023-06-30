package com.example.accountingassistant.model.menu.employee;

import com.example.accountingassistant.enums.State;
import com.example.accountingassistant.enums.calc.*;
import com.example.accountingassistant.exception.CalculationException;
import com.example.accountingassistant.model.jpa.Calculation;
import com.example.accountingassistant.model.jpa.CalculationHistoryRepository;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import com.example.accountingassistant.model.wpapper.SendMessageWrap;
import com.example.accountingassistant.service.AccountingCalculationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.util.*;

import static com.example.accountingassistant.constant.Constant.Command.COMMAND_CALCULATION;
import static com.example.accountingassistant.constant.Constant.NEW_LINE;
import static com.example.accountingassistant.enums.CalculateType.EXPERT;
import static com.example.accountingassistant.enums.CalculateType.STANDART;
import static com.example.accountingassistant.enums.State.*;
import static com.example.accountingassistant.enums.calc.Form.IP;
import static com.example.accountingassistant.enums.calc.Form.MAIN_MENU;

@Component
@Slf4j
public class MenuCalculation extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_CALCULATION;
    }

    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;

    @Autowired
    private AccountingCalculationService calculationService;

    private Map<User, Calculation> calcTmp = new HashMap();
    private Map<User, State> lastState = new HashMap();

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            val calculation = calcTmp.getOrDefault(user, new Calculation());
            if (stateService.getState(user) == FREE && lastState.get(user) != null && lastState.get(user) != FREE && lastState.get(user) != CALC_WAIT_FORM) {
                return waitContinueLogic(user, update, calculation);
            }
            if (stateService.getState(user) == CALC_WAIT_TYPE) {
                return calcWaitTypeLogic(user, update, calculation);
            }
            lastState.put(user, stateService.getState(user));
            return stateProcessing(user, update, calculation);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> stateProcessing(User user, Update update, Calculation calculation) {
        switch (stateService.getState(user)) {
            case FREE:
                return questFreeLogic(user, update, calculation);
            case CALC_WAIT_FORM:
                return questCalcWaitFormLogic(user, update, calculation);
            case CALC_WAIT_MODE:
                return questCalcWaitModeLogic(user, update, calculation);
            case CALC_WAIT_EMPLOYEE:
                return questCalcWaitEmployeeLogic(user, update, calculation);
            case CALC_WAIT_MONEY_TURNOVER:
                return questCalcWaitMoneyTurnoverLogic(user, update, calculation);
            case CALC_WAIT_OPERATION:
                return questCalcWaitOperationLogic(user, update, calculation);
            case CALC_WAIT_NDS_AGENT:
                return questCalcWaitNdsAgentLogic(user, update, calculation);
            case CALC_WAIT_NDFL_AGENT:
                return questCalcWaitNdflAgentLogic(user, update, calculation);
            case CALC_WAIT_AGENCY_CONTRACT:
                return questCalcWaitAgencyContractLogic(user, update, calculation);
            case CALC_WAIT_VED:
                return questCalcWaitVedLogic(user, update, calculation);
            case CALC_WAIT_DETACHED:
                return questCalcWaitDetachedLogic(user, update, calculation);
            case CALC_WAIT_DOCUMENT_MATCHING:
                return finishLogic(user, update, calculation);
        }
        return errorMessageDefault(update);
    }

    private List<PartialBotApiMethod> calcWaitTypeLogic(User user, Update update, Calculation calculation) {
        val btn = CalculateType.valueOf(getInputCallback(user, update));
        switch (btn) {
            case HOME:
                stateService.setState(user, FREE);
                return new ArrayList<>();
            case CONTINUE_CALCULATE:
                stateService.setState(user, lastState.get(user));
                return stateProcessing(user, update, calculation);
            case NEW_CALCULATE:
                lastState.remove(user);
                return questFreeLogic(user, update, calculation);
        }
        return errorMessageDefault(update);
    }

    private List<PartialBotApiMethod> waitContinueLogic(User user, Update update, Calculation calculation) {
        return calcBtnProcess(user, update, CalculateType.getValues(), CALC_WAIT_TYPE, 2, "У вас есть незавершенный опрос. Продолжить?");
    }

    private List<PartialBotApiMethod> questFreeLogic(User user, Update update, Calculation calculation) {
        calcTmp.put(user, calculation);
        val text = new StringBuilder();
        text.append("Для выполнения расчета необходимо ответить на 11 вопросов.").append("1/11 Укажите форму:");
        return calcBtnProcess(user, update, Form.getValues(), CALC_WAIT_FORM, 2, text.toString());
    }

    private List<PartialBotApiMethod> questCalcWaitFormLogic(User user, Update update, Calculation calculation) {
        val btn = Form.valueOf(getInputCallback(user, update));
        if (btn == MAIN_MENU) {
            stateService.setState(user, FREE);
            return new ArrayList<>();
        }
        calculation.setForm(btn);
        return calcBtnProcess(user, update, Mode.getValues(), CALC_WAIT_MODE, 2, "2/11 Укажите режим:");
    }

    private List<PartialBotApiMethod> questCalcWaitModeLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = Mode.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals("Назад")) {
                return questFreeLogic(user, update, calculation);
            }
            calculation.setMode(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        return calcBtnProcess(user, update, Employee.getValues(), CALC_WAIT_EMPLOYEE, 3, "3/11 Укажите сотрудника:");
    }

    private List<PartialBotApiMethod> questCalcWaitEmployeeLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = Employee.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals("Назад")) {
                return calcBtnProcess(user, update, Mode.getValues(), CALC_WAIT_MODE, 2, "2/11 Укажите режим:");
            }
            calculation.setEmployee(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        return calcBtnProcess(user, update, MoneyTurnover.getValues(), CALC_WAIT_MONEY_TURNOVER, 3, "4/11 Укажите оборот, млн.:");
    }

    private List<PartialBotApiMethod> questCalcWaitMoneyTurnoverLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = MoneyTurnover.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals("Назад")) {
                return calcBtnProcess(user, update, Employee.getValues(), CALC_WAIT_EMPLOYEE, 3, "3/11 Укажите сотрудника:");
            }
            calculation.setMoneyTurnover(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        return calcBtnProcess(user, update, Operation.getValues(), CALC_WAIT_OPERATION, 3, "5/11 Укажите операции:");
    }

    private List<PartialBotApiMethod> questCalcWaitOperationLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = Operation.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals("Назад")) {
                return calcBtnProcess(user, update, MoneyTurnover.getValues(), CALC_WAIT_MONEY_TURNOVER, 3, "4/11 Укажите оборот, млн.:");
            }
            calculation.setOperation(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        return calcLongProcess(user, update, CALC_WAIT_NDS_AGENT, "6/11 Введите Агента по НДС:");
    }

    private List<PartialBotApiMethod> questCalcWaitNdsAgentLogic(User user, Update update, Calculation calculation) {
        try {
            if (update.getMessage().getText().equals("/back")) {
                val answer = new ArrayList<PartialBotApiMethod>();
                answer.add(editBackMessage(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                answer.addAll(calcBtnProcess(user, update, Operation.getValues(), CALC_WAIT_OPERATION, 3, "5/11 Укажите операции:"));
                return answer;
            }
            calculation.setNdsAgent(getInputInteger(user, update));
        } catch (NullPointerException ex) {
//            ignore
        }
        return calcLongProcess(user, update, CALC_WAIT_NDFL_AGENT, "7/11 Введите Агента по НДФЛ:");
    }

    private List<PartialBotApiMethod> questCalcWaitNdflAgentLogic(User user, Update update, Calculation calculation) {
        try {
            if (update.getMessage().getText().equals("/back")) {
                val answer = new ArrayList<PartialBotApiMethod>();
                answer.add(editBackMessage(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                answer.addAll(calcLongProcess(user, update, CALC_WAIT_NDS_AGENT, "6/11 Введите Агента по НДС:"));
                return answer;
            }
            calculation.setNdflAgent(getInputInteger(user, update));
        } catch (NullPointerException ex) {
//            ignore
        }
        return calcBtnProcess(user, update, AgencyContract.getValues(), CALC_WAIT_AGENCY_CONTRACT, 2, "8/11 Укажите Агентский договор:");
    }

    private PartialBotApiMethod editBackMessage(Long chatId, Integer messageId) {
        return SendMessageWrap.init()
                .setChatIdLong(chatId)
                .setText("Выбрано меню: назад")
                .build().createSendMessage();
    }

    private List<PartialBotApiMethod> questCalcWaitAgencyContractLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = AgencyContract.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals("Назад")) {
                return calcLongProcess(user, update, CALC_WAIT_NDFL_AGENT, "7/11 Введите Агента по НДФЛ:");
            }
            calculation.setAgencyContract(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        return calcBtnProcess(user, update, VED.getValues(), CALC_WAIT_VED, 3, "9/11 Укажите ВЭД:");
    }

    private List<PartialBotApiMethod> questCalcWaitVedLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = VED.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals("Назад")) {
                return calcBtnProcess(user, update, AgencyContract.getValues(), CALC_WAIT_AGENCY_CONTRACT, 2, "8/11 Укажите Агентский договор:");
            }
            calculation.setVed(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        if(calculation.getForm() == IP){
            calculation.setDetached(0);
            return calcBtnProcess(user, update, DocumentMatching.getValues(), CALC_WAIT_DOCUMENT_MATCHING, 2, "11/11 укажите сопоставление документов:");
        }
        return calcLongProcess(user, update, CALC_WAIT_DETACHED, "10/11 Введите обособленное:");
    }

    private List<PartialBotApiMethod> questCalcWaitDetachedLogic(User user, Update update, Calculation calculation) {
        try {
            if (update.getMessage().getText().equals("/back")) {
                val answer = new ArrayList<PartialBotApiMethod>();
                answer.add(editBackMessage(update.getMessage().getChatId(), update.getMessage().getMessageId()));
                if(calculation.getForm() == IP){
                    answer.addAll(calcBtnProcess(user, update, AgencyContract.getValues(), CALC_WAIT_AGENCY_CONTRACT, 2, "8/11 Укажите Агентский договор:"));
                } else{
                    answer.addAll(calcBtnProcess(user, update, VED.getValues(), CALC_WAIT_VED, 3, "9/11 Укажите ВЭД:"));
                }
                return answer;
            }
            calculation.setDetached(getInputInteger(user, update));
        } catch (NullPointerException ex) {
//            ignore
        }
        return calcBtnProcess(user, update, DocumentMatching.getValues(), CALC_WAIT_DOCUMENT_MATCHING, 2, "11/11 укажите сопоставление документов:");
    }

    private List<PartialBotApiMethod> finishLogic(User user, Update update, Calculation calculation) {
        val text = new StringBuilder();
        val btn = DocumentMatching.valueOf(getInputCallback(user, update));
        if (btn.getTitle().equals("Назад")) {
            return calcLongProcess(user, update, CALC_WAIT_DETACHED, "10/11 Введите обособленное:");
        }
        calculation.setDocumentMatching(btn);
        try {
            val calcStandart = calculationService.calculate(calculation, STANDART);
            val calcExpert = calculationService.calculate(calculation, EXPERT);
            calculation.setResultStandart(calcStandart);
            calculation.setResultExpert(calcExpert);
            text.append("Расчет по введенным данным завершен.").append(NEW_LINE)
                    .append(" - Стандарт: ").append(calcStandart).append(NEW_LINE)
                    .append(" - Эксперт: ").append(calcExpert);
        } catch (CalculationException ex) {
            log.info("Неудачный расчет с параметрами:" + calculation);
            text.append("Расчет по введенным данным выполнить невозможно.");
        }
        calculation.setUser(user);
        calculation.setCalculationDate(new Timestamp(System.currentTimeMillis()));
        calculationHistoryRepository.save(calculation);
        calcTmp.remove(user);
        lastState.remove(user);
        stateService.refreshUser(user);

        val answer = Arrays.asList(SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(text.toString())
                        .build().createSendMessage()
                , getMessageContact(user, update));
        return answer;
    }

    private List<PartialBotApiMethod> calcBtnProcess(User user, Update update, Map btns, State state, int countColumn, String text) {
        stateService.setState(user, state);
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString())
                .setInlineKeyboardMarkup(buttonService.createVerticalColumnMenu(countColumn, btns))
                .build().createSendMessage());
    }

    private List<PartialBotApiMethod> calcLongProcess(User user, Update update, State state, String text) {
        stateService.setState(user, state);
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString() + NEW_LINE + " Шаг назад: /back")
//                .setInlineKeyboardMarkup(buttonService.createVerticalColumnMenu(1, Collections.singletonMap("back", "назад")))
                .build().createSendMessage());
    }

    @Override
    public String getDescription() {
        return "Выполнить расчет";
    }
}
