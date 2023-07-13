package com.example.accountingassistant.model.jpa;

import com.example.accountingassistant.enums.LeadExportStatus;
import com.example.accountingassistant.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Objects;

import static org.apache.el.parser.ELParserConstants.EMPTY;

@Getter
@Setter
@ToString
@Entity(name = "user_tbl")
public class User {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "fio")
    private String fio;

    @Column(name = "phone")
    private String phone;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "lead_export_status")
    private LeadExportStatus leadExportStatus;

    @Column(name = "user_role")
    private UserRole userRole;
    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", userRole=" + userRole.getTitle() +
                ", registeredAt=" + registeredAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getChatId().equals(user.getChatId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChatId());
    }

    public String getNameOrFirst() {
        return userName != null && !userName.equals(EMPTY) ? userName : firstName;
    }
}
