package com.example.accountingassistant.enums;

public enum UserRole {

    BLOCKED("Заблокирован"),
    EMPLOYEE("Пользователь"),
    ADMIN("Администратор");

    private String title;

    UserRole(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
