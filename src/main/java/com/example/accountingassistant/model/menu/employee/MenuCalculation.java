package com.example.accountingassistant.model.menu.employee;

import com.example.accountingassistant.enums.State;
import com.example.accountingassistant.enums.calc.*;
import com.example.accountingassistant.exception.CalculationException;
import com.example.accountingassistant.model.jpa.Calculation;
import com.example.accountingassistant.model.jpa.CalculationHistoryRepository;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.menu.base.Menu;
import com.example.accountingassistant.service.AccountingCalculationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.tgcommons.model.button.Button;
import org.example.tgcommons.model.button.ButtonsDescription;
import org.example.tgcommons.model.wrapper.EditMessageTextWrap;
import org.example.tgcommons.model.wrapper.SendMessageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Timestamp;
import java.util.*;

import static com.example.accountingassistant.constant.Constant.BACK;
import static com.example.accountingassistant.constant.Constant.Command.COMMAND_CALCULATION;
import static com.example.accountingassistant.enums.CalculateType.STANDART;
import static com.example.accountingassistant.enums.State.*;
import static com.example.accountingassistant.enums.calc.Form.IP;
import static com.example.accountingassistant.enums.calc.Form.MAIN_MENU;
import static org.example.tgcommons.constant.Constant.TextConstants.NEW_LINE;
import static org.example.tgcommons.utils.ButtonUtils.createVerticalColumnMenu;

@Component(COMMAND_CALCULATION)
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

    private final Map<User, Calculation> calcTmp = new HashMap<>();
    private final Map<User, State> lastState = new HashMap<>();
    private final Map<User, String> lastButtonMenu = new HashMap<>();

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            val calculation = calcTmp.getOrDefault(user, new Calculation());
            if (stateService.getState(user) == FREE && lastState.get(user) != null && lastState.get(user) != FREE && lastState.get(user) != CALC_WAIT_FORM) {
                return waitContinueLogic(user);
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

    @Override
    public PartialBotApiMethod replaceButton(Update update, User user) {
        if (!update.hasCallbackQuery()) {
            return null;
        }
        val message = update.getCallbackQuery().getMessage();
        val menuName = message.getReplyMarkup().getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(e -> e.getCallbackData().equals(update.getCallbackQuery().getData()))
                .findFirst().get().getText();
        return EditMessageTextWrap.init()
                .setChatIdLong(message.getChatId())
                .setMessageId(message.getMessageId())
                .setText(lastButtonMenu.get(user) + ": " + menuName)
                .build().createMessage();
    }

    private List<PartialBotApiMethod> stateProcessing(User user, Update update, Calculation calculation) {
        return switch (stateService.getState(user)) {
            case FREE -> questFreeLogic(user, calculation);
            case CALC_WAIT_FORM -> questCalcWaitFormLogic(user, update, calculation);
            case CALC_WAIT_MODE -> questCalcWaitModeLogic(user, update, calculation);
            case CALC_WAIT_EMPLOYEE -> questCalcWaitEmployeeLogic(user, update, calculation);
            case CALC_WAIT_MONEY_TURNOVER -> questCalcWaitMoneyTurnoverLogic(user, update, calculation);
            case CALC_WAIT_OPERATION -> questCalcWaitOperationLogic(user, update, calculation);
            case CALC_WAIT_AGENCY_CONTRACT -> questCalcWaitAgencyContractLogic(user, update, calculation);
            case CALC_WAIT_VED -> questCalcWaitVedLogic(user, update, calculation);
            case CALC_WAIT_DETACHED -> questCalcWaitDetachedLogic(user, update, calculation);
            case CALC_WAIT_DOCUMENT_MATCHING -> finishLogic(user, update, calculation);
            default -> errorMessageDefault(update);
        };
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
                return questFreeLogic(user, calculation);
        }
        return errorMessageDefault(update);
    }

    private List<PartialBotApiMethod> waitContinueLogic(User user) {
        return calcBtnProcess(user, CalculateType.getValues(), CALC_WAIT_TYPE, 2, "У вас есть незавершенный опрос. Продолжить?");
    }

    private List<PartialBotApiMethod> questFreeLogic(User user, Calculation calculation) {
        calcTmp.put(user, calculation);
        String text = "Для выполнения расчета необходимо ответить на 11 вопросов." + NEW_LINE + "1/9 Укажите форму:";
        lastButtonMenu.put(user, "Форма");
        return calcBtnProcess(user, Form.getValues(), CALC_WAIT_FORM, 2, text);
    }

    private List<PartialBotApiMethod> questCalcWaitFormLogic(User user, Update update, Calculation calculation) {
        val btn = Form.valueOf(getInputCallback(user, update));
        if (btn == MAIN_MENU) {
            stateService.setState(user, FREE);
            return new ArrayList<>();
        }
        calculation.setForm(btn);
        lastButtonMenu.put(user, "Режим");
        return calcBtnProcess(user, Mode.getValues(), CALC_WAIT_MODE, 1, "2/9 Укажите режим:");
    }

    private List<PartialBotApiMethod> questCalcWaitModeLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = Mode.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals(BACK)) {
                return questFreeLogic(user, calculation);
            }
            calculation.setMode(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        lastButtonMenu.put(user, "Количество сотрудников");
        return calcBtnProcess(user, Employee.getValues(), CALC_WAIT_EMPLOYEE, 3, "3/9 Укажите количество сотрудников:");
    }

    private List<PartialBotApiMethod> questCalcWaitEmployeeLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = Employee.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals(BACK)) {
                lastButtonMenu.put(user, "Режим");
                return calcBtnProcess(user, Mode.getValues(), CALC_WAIT_MODE, 1, "2/9 Укажите режим:");
            }
            calculation.setEmployee(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        lastButtonMenu.put(user, "Оборот в мес. млн.");
        return calcBtnProcess(user, MoneyTurnover.getValues(), CALC_WAIT_MONEY_TURNOVER, 3, "4/9 Укажите оборот в мес. млн.:");
    }

    private List<PartialBotApiMethod> questCalcWaitMoneyTurnoverLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = MoneyTurnover.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals(BACK)) {
                lastButtonMenu.put(user, "Количество сотрудников");
                return calcBtnProcess(user, Employee.getValues(), CALC_WAIT_EMPLOYEE, 3, "3/9 Укажите количество сотрудников:");
            }
            calculation.setMoneyTurnover(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        lastButtonMenu.put(user, "Количество документов в месяц");
        return calcBtnProcess(user, Operation.getValues(), CALC_WAIT_OPERATION, 3, "5/9 Укажите количество документов в месяц:");
    }

    private List<PartialBotApiMethod> questCalcWaitOperationLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = Operation.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals(BACK)) {
                lastButtonMenu.put(user, "Оборот в мес. млн.");
                return calcBtnProcess(user, MoneyTurnover.getValues(), CALC_WAIT_MONEY_TURNOVER, 3, "4/9 Укажите оборот, млн.:");
            }
            calculation.setOperation(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        lastButtonMenu.put(user, "Количество артикулов в Агентском договоре");
        return calcBtnProcess(user, AgencyContract.getValues(), CALC_WAIT_AGENCY_CONTRACT, 2, "6/9 Укажите количество артикулов в Агентском договоре:");
    }

    private List<PartialBotApiMethod> questCalcWaitAgencyContractLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = AgencyContract.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals(BACK)) {
                lastButtonMenu.put(user, "Количество документов в месяц");
                return calcBtnProcess(user, Operation.getValues(), CALC_WAIT_OPERATION, 3, "5/9 Укажите количество документов в месяц:");
            }
            calculation.setAgencyContract(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        lastButtonMenu.put(user, "ВЭД");
        return calcBtnProcess(user, VED.getValues(), CALC_WAIT_VED, 3, "7/9 Укажите ВЭД:");
    }

    private List<PartialBotApiMethod> questCalcWaitVedLogic(User user, Update update, Calculation calculation) {
        try {
            val btn = VED.valueOf(getInputCallback(user, update));
            if (btn.getTitle().equals(BACK)) {
                lastButtonMenu.put(user, "Количество артикулов в Агентском договоре");
                return calcBtnProcess(user, AgencyContract.getValues(), CALC_WAIT_AGENCY_CONTRACT, 2, "6/9 Укажите количество артикулов в Агентском договоре:");
            }
            calculation.setVed(btn);
        } catch (IllegalArgumentException ex) {
//            ignore
        }
        if (calculation.getForm() == IP) {
            calculation.setDetached(0);
            lastButtonMenu.put(user, "Количество касс");
            return calcBtnProcess(user, CashBox.getValues(), CALC_WAIT_DOCUMENT_MATCHING, 2, "9/9 Укажите количество касс:");
        }
        lastButtonMenu.put(user, "Количество обособленных подразделений, количество площадок");
        return calcLongProcess(user, CALC_WAIT_DETACHED, "8/9 Введите количество обособленных подразделений, количество площадок:");
    }

    private List<PartialBotApiMethod> questCalcWaitDetachedLogic(User user, Update update, Calculation calculation) {
        try {
            if (update.getMessage().getText().equals("/back")) {
                val answer = new ArrayList<PartialBotApiMethod>();
                answer.add(editBackMessage(update.getMessage().getChatId()));
                lastButtonMenu.put(user, "ВЭД");
                answer.addAll(calcBtnProcess(user, VED.getValues(), CALC_WAIT_VED, 3, "7/9 Укажите ВЭД:"));
                return answer;
            }
            calculation.setDetached(getInputInteger(user, update));
        } catch (NullPointerException ex) {
//            ignore
        }
        lastButtonMenu.put(user, "Количество касс");
        return calcBtnProcess(user, CashBox.getValues(), CALC_WAIT_DOCUMENT_MATCHING, 2, "9/9 Укажите количество касс:");
    }

    private List<PartialBotApiMethod> finishLogic(User user, Update update, Calculation calculation) {
        val text = new StringBuilder();
        val btn = CashBox.valueOf(getInputCallback(user, update));
        if (btn.getTitle().equals(BACK)) {
            if (calculation.getForm() == IP) {
                lastButtonMenu.put(user, "ВЭД");
                return calcBtnProcess(user, VED.getValues(), CALC_WAIT_VED, 3, "7/9 Укажите ВЭД:");
            } else {
                lastButtonMenu.put(user, "Количество обособленных подразделений, количество площадок");
                return calcLongProcess(user, CALC_WAIT_DETACHED, "8/9 Введите количество обособленных подразделений, количество площадок:");
            }
        }
        calculation.setCashBox(btn);
        calculation.setNdsAgent(0);
        calculation.setNdflAgent(0);
        try {
            val calcStandart = calculationService.calculate(calculation, STANDART) / 100 * 100 + 90;
            val calcExpert = ((int) (calcStandart + calcStandart * 40.0 / 100)) / 100 * 100 + 90;

            calculation.setResultStandart(calcStandart);
            calculation.setResultExpert(calcExpert);
            text.append("Расчет по введенным данным завершен.").append(NEW_LINE)
                    .append(" - Стандарт: от ").append(calcStandart).append(NEW_LINE)
                    .append(" - Эксперт: от ").append(calcExpert);
        } catch (CalculationException ex) {
            log.info("Неудачный расчет с параметрами:" + calculation);
            text.append("Проконсультироваться по индивидуальному запросу");
        }
        calculation.setUser(user);
        calculation.setCalculationDate(new Timestamp(System.currentTimeMillis()));
        calculationHistoryRepository.save(calculation);
        calcTmp.remove(user);
        lastState.remove(user);
        stateService.refreshUser(user);

        return Arrays.asList(SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(text.toString())
                        .build().createMessage()
                , getMessageContact(user, update));
    }

    private PartialBotApiMethod editBackMessage(Long chatId) {
        return SendMessageWrap.init()
                .setChatIdLong(chatId)
                .setText("Выбрано меню: назад")
                .build().createMessage();
    }

    private List<PartialBotApiMethod> calcBtnProcess(User user, Map<String, String> btns, State state, int countColumn, String text) {
        val buttons = new ArrayList<Button>();
        for (val entry : btns.entrySet()) {
            buttons.add(Button.init().setKey(entry.getKey()).setValue(entry.getValue()).build());
        }
        val buttonsDescription = ButtonsDescription.init()
                .setCountColumn(countColumn)
                .setButtons(buttons)
                .build();

        stateService.setState(user, state);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text)
                .setInlineKeyboardMarkup(createVerticalColumnMenu(buttonsDescription))
                .build().createMessageList();
    }

    private List<PartialBotApiMethod> calcLongProcess(User user, State state, String text) {
        stateService.setState(user, state);
        return SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text + NEW_LINE + " Шаг назад: /back")
                .build().createMessageList();
    }

    @Override
    public String getDescription() {
        return "Выполнить расчет";
    }
}
