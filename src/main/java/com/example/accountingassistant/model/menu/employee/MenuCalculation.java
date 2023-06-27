package com.example.accountingassistant.model.menu.employee;

import com.example.accountingassistant.enums.CalculateType;
import com.example.accountingassistant.enums.State;
import com.example.accountingassistant.enums.calc.*;
import com.example.accountingassistant.model.jpa.Calculation;
import com.example.accountingassistant.model.jpa.CalculationHistoryRepository;
import com.example.accountingassistant.model.jpa.User;
import com.example.accountingassistant.model.jpa.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private AccountingCalculationService calculationService;

    private Map<User, Calculation> calcTmp = new HashMap();

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        val calculation = calcTmp.getOrDefault(user, new Calculation());
        try {
            switch (stateService.getState(user)) {
                case FREE:
                    calcTmp.put(user, calculation);
                    val text = new StringBuilder();
                    text.append("Для выполнения расчета необходимо ответить на 11 вопросов.")
                            .append("1/11 Укажите форму:");
                    return calcBtnProcess(user, update, Form.getValues(), CALC_WAIT_FORM, text.toString());
                case CALC_WAIT_FORM:
                    calculation.setForm(Form.valueOf(update.getCallbackQuery().getData()));
                    return calcBtnProcess(user, update, Mode.getValues(), CALC_WAIT_MODE, "2/11 Укажите режим:");
                case CALC_WAIT_MODE:
                    calculation.setMode(Mode.valueOf(update.getCallbackQuery().getData()));
                    return calcBtnProcess(user, update, Employee.getValues(), CALC_WAIT_EMPLOYEE, "3/11 Укажите сотрудника:");
                case CALC_WAIT_EMPLOYEE:
                    calculation.setEmployee(Employee.valueOf(update.getCallbackQuery().getData()));
                    return calcBtnProcess(user, update, MoneyTurnover.getValues(), CALC_WAIT_MONEY_TURNOVER, "4/11 Укажите оборот, млн.:");
                case CALC_WAIT_MONEY_TURNOVER:
                    calculation.setMoneyTurnover(MoneyTurnover.valueOf(update.getCallbackQuery().getData()));
                    return calcBtnProcess(user, update, Operation.getValues(), CALC_WAIT_OPERATION, "5/11 Укажите операции:");
                case CALC_WAIT_OPERATION:
                    calculation.setOperation(Operation.valueOf(update.getCallbackQuery().getData()));
                    return calcLongProcess(user, update, CALC_WAIT_NDS_AGENT, "6/11 Введите Агента по НДС:");
                case CALC_WAIT_NDS_AGENT:
                    calculation.setNdsAgent(Integer.parseInt(update.getMessage().getText()));
                    return calcLongProcess(user, update, CALC_WAIT_NDFL_AGENT, "7/11 Введите Агента по НДФЛ:");
                case CALC_WAIT_NDFL_AGENT:
                    calculation.setNdflAgent(Integer.parseInt(update.getMessage().getText()));
                    return calcBtnProcess(user, update, AgencyContract.getValues(), CALC_WAIT_AGENCY_CONTRACT, "8/11 Укажите Агентский договор:");
                case CALC_WAIT_AGENCY_CONTRACT:
                    calculation.setAgencyContract(AgencyContract.valueOf(update.getCallbackQuery().getData()));
                    return calcBtnProcess(user, update, VED.getValues(), CALC_WAIT_VED, "9/11 Укажите ВЭД:");
                case CALC_WAIT_VED:
                    calculation.setVed(VED.valueOf(update.getCallbackQuery().getData()));
                    return calcLongProcess(user, update, CALC_WAIT_DETACHED, "10/11 Введите обособленное:");
                case CALC_WAIT_DETACHED:
                    calculation.setDetached(Integer.parseInt(update.getMessage().getText()));
                    return calcBtnProcess(user, update, DocumentMatching.getValues(), CALC_WAIT_DOCUMENT_MATCHING, "11/11 укажите сопоставление документов:");
                case CALC_WAIT_DOCUMENT_MATCHING:
                    return finishLogic(user, update, calculation);
            }
            return errorMessageDefault(update);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
    }

    private List<PartialBotApiMethod> finishLogic(User user, Update update, Calculation calculation) {
        calculation.setDocumentMatching(DocumentMatching.valueOf(update.getCallbackQuery().getData()));
        val calcStandart = calculationService.calculate(calculation, STANDART);
        val calcExpert = calculationService.calculate(calculation, EXPERT);
        calculation.setResultStandart(calcStandart);
        calculation.setResultExpert(calcExpert);
        calculation.setChatId(user.getChatId());
        calculation.setCalculationDate(new Timestamp(System.currentTimeMillis()));
        calculationHistoryRepository.save(calculation);
        calcTmp.remove(user);
        stateService.setState(user, FREE);
        val text = new StringBuilder();
        text.append("Расчет по введенным данным завершен.").append(NEW_LINE)
                .append(" - Стандарт: ").append(calcStandart).append(NEW_LINE)
                .append(" - Эксперт: ").append(calcExpert);

        val answer = Arrays.asList(SendMessageWrap.init()
                        .setChatIdLong(user.getChatId())
                        .setText(text.toString())
                        .build().createSendMessage()
                , getMessageContact(user, update));
        return answer;
    }

    private List<PartialBotApiMethod> calcBtnProcess(User user, Update update, Map btns, State state, String text) {
        stateService.setState(user, state);
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString())
                .setInlineKeyboardMarkup(buttonService.createVerticalMenu(btns))
                .build().createSendMessage());
    }

    private List<PartialBotApiMethod> calcLongProcess(User user, Update update, State state, String text) {
        stateService.setState(user, state);
        return Arrays.asList(SendMessageWrap.init()
                .setChatIdLong(user.getChatId())
                .setText(text.toString())
                .build().createSendMessage());
    }


//    private class HistoryActionDateComparator implements Comparator<HistoryAction> {
//        @Override
//        public int compare(HistoryAction o1, HistoryAction o2) {
//            return o1.getActionDate().compareTo(o2.getActionDate());
//        }
//    }

//    private List<PartialBotApiMethod> historyWaitUserLogic(User user, Update update) {
//        if (!update.hasCallbackQuery()) {
//            return errorMessageDefault(update);
//        }
//        val userEmployeeChatId = update.getCallbackQuery().getData();
//        val dateStart = DateUtils.addDays(new Date(), -7);
//        val userEmployee = userRepository.findUserByChatId(Integer.parseInt(userEmployeeChatId));
//        val historyActionsFrom = historyActionRepository.findByChatIdFromEqualsAndActionDateAfter(
//                Integer.parseInt(userEmployeeChatId), dateStart);
//        val historyActionsTo = historyActionRepository.findByChatIdToEqualsAndActionDateAfter(
//                Integer.parseInt(userEmployeeChatId), dateStart);
//        val historyActions = new ArrayList<HistoryAction>();
//        historyActions.addAll(historyActionsFrom);
//        historyActions.addAll(historyActionsTo);
//        val comparator = new HistoryActionDateComparator();
//        historyActionsTo.stream().sorted(comparator);
//        val answer = new StringBuilder();
//        answer.append("Действия пользователя: " + prepareShield(userEmployee.getNameOrFirst())).append(SPACE);
//        if (historyActionsTo.size() == 0) {
//            answer.append("не найдены");
//        }
//        answer.append(NEW_LINE);
//        for (HistoryAction historyAction : historyActionsTo) {
//            answer.append(historyAction.getActionDate()).append(SPACE)
//                    .append(historyAction.getActionType() == USER_ACTION ? "исх: " : "вх: ");
//            if (historyAction.getMessageText() != null) {
//                answer.append(prepareShield(historyAction.getMessageText())).append(SPACE);
//            }
//            if (historyAction.getCallbackMenuName() != null) {
//                answer.append(prepareShield(historyAction.getCallbackMenuName())).append(SPACE);
//            }
//            if (historyAction.getFileName() != null) {
//                answer.append(prepareShield(historyAction.getFileName())).append(SPACE);
//            }
//            answer.append(NEW_LINE).append(NEW_LINE);
//        }
//        stateService.setState(user, FREE);
//        return List.of(SendMessageWrap.init()
//                .setChatIdLong(user.getChatId())
//                .setText(answer.toString())
//                .build().createSendMessage());
//    }


    //    private List<PartialBotApiMethod> gerFreeLogicSupport(User user, Update update) {
//        val companys = companyRepository.findAll();
//        if (companys.size() == 0) {
//            return Arrays.asList(SendMessageWrap.init()
//                    .setChatIdLong(user.getChatId())
//                    .setText("Компании отсутствуют")
//                    .build().createSendMessage());
//        }
//        val btns = new LinkedHashMap<String, String>();
//        for (int i = 0; i < companys.size(); ++i) {
//            btns.put(String.valueOf(companys.get(i).getCompanyId()), prepareShield(companys.get(i).getCompanyName()));
//        }
//        stateService.setState(user, HISTORY_WAIT_COMPANY);
//        return Arrays.asList(SendMessageWrap.init()
//                .setChatIdLong(update.getMessage().getChatId())
//                .setText("Выберите компанию:")
//                .setInlineKeyboardMarkup(buttonService.createVerticalMenu(btns))
//                .build().createSendMessage());
//    }
//
//    private List<PartialBotApiMethod> historyWaitCompanyLogic(User user, Update update) {
//        if (!update.hasCallbackQuery()) {
//            return errorMessageDefault(update);
//        }
//        val company = companyRepository.findCompanyByCompanyId(Integer.parseInt(update.getCallbackQuery().getData()));
//        val users = userRepository.findUserByCompany(company);
//        return showUsers(user, users);
//    }
//    private List<PartialBotApiMethod> getFreeLogicMainEmployee(User user, Update update) {
//        val users = userRepository.findUserByCompanyAndAndUserRole(user.getCompany(), EMPLOYEE);
//        return showUsers(user, users);
//    }
//

    @Override
    public String getDescription() {
        return "Выполнить расчет";
    }
}
