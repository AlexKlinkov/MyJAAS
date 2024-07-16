package ru.inner.project.MyJAAS.utils;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomInitialization { // The class is being used to create the first admin in system

    @Transactional
    public void initFirstAdminInBD(EntityManager entityManager, String adminName, String adminEmail, Integer adminAge,
                                   String adminLogin, String adminPassword, String adminSubscription,
                                   boolean isLockedAccount, String adminRole) {
        log.debug("Start method initFirstAdminInBD " + LocalDateTime.now());
        log.debug("Try fill up user table " + LocalDateTime.now());
        BigInteger newId = BigInteger.ONE;
        try {
            String insertSql = "INSERT INTO users (username, email, age) " +
                    "VALUES (:username, :email, :age) ON CONFLICT DO NOTHING";
            entityManager.createNativeQuery(insertSql)
                    .setParameter("username", adminName)
                    .setParameter("email", adminEmail)
                    .setParameter("age", adminAge)
                    .executeUpdate();
        } catch (Exception e) {
            log.debug("Exception with fill up user table " + LocalDateTime.now() + '\n', e);
        }
        log.debug("Try fill up account table " + LocalDateTime.now());
        try {
            String insertSql = "INSERT INTO account (user_id, login, password, subscription, is_locked_account) " +
                    "VALUES (:user_id, :login, :password, :subscription, :is_locked_account) ON CONFLICT DO NOTHING";
            entityManager.createNativeQuery(insertSql)
                    .setParameter("user_id", newId)
                    .setParameter("login", adminLogin)
                    .setParameter("password", adminPassword)
                    .setParameter("subscription", adminSubscription)
                    .setParameter("is_locked_account", isLockedAccount)
                    .executeUpdate();
        } catch (Exception e) {
            log.debug("Exception with fill up account table " + LocalDateTime.now() + '\n', e);
        }
        log.debug("Try fill up user_role table " + LocalDateTime.now());
        try {
            String insertSql = "INSERT INTO user_role (user_id, role) VALUES (:user_id, :role) ON CONFLICT DO NOTHING";
            entityManager.createNativeQuery(insertSql)
                    .setParameter("user_id", newId)
                    .setParameter("role", adminRole.toUpperCase())
                    .executeUpdate();
        } catch (Exception e) {
            log.debug("Exception with fill up user_role table " + LocalDateTime.now() + '\n', e);
        }
    }
}
