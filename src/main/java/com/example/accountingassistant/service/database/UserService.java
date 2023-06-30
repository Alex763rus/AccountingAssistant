package com.example.accountingassistant.service.database;

import com.example.accountingassistant.enums.LeadExportStatus;
import com.example.accountingassistant.model.jpa.*;
import com.example.accountingassistant.service.menu.StateService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;
import java.util.List;

import static com.example.accountingassistant.enums.LeadExportStatus.NEW_LEAD;
import static com.example.accountingassistant.enums.UserRole.UNREGISTERED;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StateService stateService;

    public User getUser(Update update) {
        val message = getMessage(update);
        val chatId = message.getChatId();
        User user = stateService.getUser(chatId);
        if (user == null) {
            val users = userRepository.findById(chatId);
            user = users.isPresent() ? users.get() : registeredUser(message);
        }
        return user;
    }

    public void refreshUser(User user) {
        stateService.refreshUser(user);
    }

    private Message getMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage();
        }
        return null;
    }

    private User registeredUser(Message message) {
        val chatId = message.getChatId();
        val chat = message.getChat();

        User user = new User();

        user.setChatId(chatId);
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUserName(chat.getUserName());
        user.setUserRole(UNREGISTERED);
        user.setLeadExportStatus(NEW_LEAD);
        user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

        userRepository.save(user);
        log.info("user saved: " + user);
        return user;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void saveUser(List<User> users) {
        userRepository.saveAll(users);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findUserByLeadExportStatus(LeadExportStatus leadExportStatus) {
        return userRepository.findUserByLeadExportStatus(leadExportStatus);
    }
}
