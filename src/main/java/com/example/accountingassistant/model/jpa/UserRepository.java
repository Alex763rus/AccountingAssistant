package com.example.accountingassistant.model.jpa;

import com.example.accountingassistant.enums.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findUserByUserRole(UserRole userRole);
    User findUserByChatId(Long chatId);

    List<User> findAll();

}
