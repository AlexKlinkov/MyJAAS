package ru.inner.project.MyJAAS.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.inner.project.MyJAAS.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByLogin(String login);
    Account findAccountByUserId(Long userId);
}
