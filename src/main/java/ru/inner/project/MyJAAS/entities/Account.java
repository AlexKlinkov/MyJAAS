package ru.inner.project.MyJAAS.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    private String login;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "password")
    private String password;
    @Column(name = "subscription")
    private String subscription;
    @Column(name = "is_locked_account")
    private Boolean isLockedAccount;
    @Column(name = "is_logged_out")
    private Boolean isLoggedOut;
}
