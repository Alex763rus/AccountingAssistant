package com.example.accountingassistant.model.menu;

import com.example.accountingassistant.model.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

import static com.example.accountingassistant.constant.Constant.Command.*;

@Component
@Slf4j
public class MenuCalculationHistory extends Menu {

    @Override
    public String getMenuComand() {
        return COMMAND_CALCULATION_HISTORY;
    }

    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PartialBotApiMethod> menuRun(User user, Update update) {
        try {
            switch (stateService.getState(user)) {
//                case FREE:
//                    return freelogic(user, update);
//                case HISTORY_WAIT_USER:
//                    return historyWaitUserLogic(user, update);
//                case HISTORY_WAIT_COMPANY:
//                    return historyWaitCompanyLogic(user, update);
            }
            return errorMessageDefault(update);
        } catch (Exception ex) {
            log.error(ex.toString());
            return errorMessage(update, ex.toString());
        }
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
//        val userEmployee = userRepository.findUserByChatId(Long.parseLong(userEmployeeChatId));
//        val historyActionsFrom = historyActionRepository.findByChatIdFromEqualsAndActionDateAfter(
//                Long.parseLong(userEmployeeChatId), dateStart);
//        val historyActionsTo = historyActionRepository.findByChatIdToEqualsAndActionDateAfter(
//                Long.parseLong(userEmployeeChatId), dateStart);
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

//    private List<PartialBotApiMethod> freelogic(User user, Update update) {
//        switch (user.getUserRole()) {
//            case MAIN_EMPLOYEE:
//                return getFreeLogicMainEmployee(user, update);
//            case SUPPORT:
//                return gerFreeLogicSupport(user, update);
//            default:
//                return errorMessage(update, "У вашей роли не должно быть сюда доступа.." + user.getUserRole());
//        }
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
//        val company = companyRepository.findCompanyByCompanyId(Long.parseLong(update.getCallbackQuery().getData()));
//        val users = userRepository.findUserByCompany(company);
//        return showUsers(user, users);
//    }
//    private List<PartialBotApiMethod> getFreeLogicMainEmployee(User user, Update update) {
//        val users = userRepository.findUserByCompanyAndAndUserRole(user.getCompany(), EMPLOYEE);
//        return showUsers(user, users);
//    }
//
//    private List<PartialBotApiMethod> showUsers(User user, List<User> users) {
//        if (users.size() == 0) {
//            return Arrays.asList(SendMessageWrap.init()
//                    .setChatIdLong(user.getChatId())
//                    .setText("В выбранной компании пользователи не найдены")
//                    .build().createSendMessage());
//        }
//        stateService.setState(user, HISTORY_WAIT_USER);
//        val btns = new LinkedHashMap<String, String>();
//        for (int i = 0; i < users.size(); ++i) {
//            btns.put(String.valueOf(users.get(i).getChatId()), prepareShield(users.get(i).getNameOrFirst()));
//        }
//        return Arrays.asList(SendMessageWrap.init()
//                .setChatIdLong(user.getChatId())
//                .setText("Выберите сотрудника:")
//                .setInlineKeyboardMarkup(buttonService.createVerticalMenu(btns))
//                .build().createSendMessage());
//    }
    @Override
    public String getDescription() {
        return "История рсчетов";
    }
}
