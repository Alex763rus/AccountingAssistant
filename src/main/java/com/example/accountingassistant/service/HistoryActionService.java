package com.example.accountingassistant.service;

import com.example.accountingassistant.model.jpa.CalculationHistory;
import com.example.accountingassistant.model.jpa.CalculationHistoryRepository;
import com.example.accountingassistant.model.jpa.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class HistoryActionService {

    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;

    public void saveHistoryAction(User user, Update update) {
        val historyAction = new CalculationHistory();
        historyAction.setActionDate(new Timestamp(System.currentTimeMillis()));
        historyAction.setChatIdFrom(user.getChatId());
        if (update.hasMessage()) {
            val message = update.getMessage();
            if (message.hasText()) {
                if (message.getText().contains("Главное")) {
                    return;
                }
                historyAction.setMessageText(message.getText());
            }
            if (message.hasDocument()) {
                historyAction.setMessageText(message.getCaption());
                historyAction.setFileName(update.getMessage().getText());
            }
        }
        if (update.hasCallbackQuery()) {
            val menuName = update.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard().stream()
                    .filter(e -> e.get(0).getCallbackData().equals(update.getCallbackQuery().getData()))
                    .findFirst().get().get(0).getText();
            historyAction.setCallbackMenuName(menuName);
        }
        calculationHistoryRepository.save(historyAction);
    }

    public void saveHistoryAnswerAction(User user, List<PartialBotApiMethod> answers) {
        for (PartialBotApiMethod answer : answers) {
            saveHistoryAnswerAction(user, answer);
        }
    }

    private void saveHistoryAnswerAction(User user, PartialBotApiMethod partialBotApiMethod) {
        val historyAction = new CalculationHistory();
        historyAction.setActionDate(new Timestamp(System.currentTimeMillis()));
        historyAction.setChatIdFrom(user.getChatId());
        if (partialBotApiMethod instanceof SendMessage) {
            val answer = (SendMessage) partialBotApiMethod;
            if (answer.getText().contains("Главное")) {
                return;
            }
            historyAction.setMessageText(answer.getText());
            historyAction.setChatIdTo(Long.parseLong(answer.getChatId()));
        }
        if (partialBotApiMethod instanceof EditMessageText) {
            val answer = (EditMessageText) partialBotApiMethod;
            if (answer.getText().contains("Главное")) {
                return;
            }
            historyAction.setMessageText(answer.getText());
            historyAction.setChatIdTo(Long.parseLong(answer.getChatId()));
        }
        if (partialBotApiMethod instanceof SendDocument) {
            val answer = (SendDocument) partialBotApiMethod;
            historyAction.setMessageText(answer.getCaption());
            historyAction.setChatIdTo(Long.parseLong(answer.getChatId()));
            if (answer.getDocument() != null) {
                historyAction.setFileName(answer.getDocument().getAttachName());
            }
        }
        calculationHistoryRepository.save(historyAction);
    }
}
