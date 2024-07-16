package ru.inner.project.MyJAAS.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.inner.project.MyJAAS.configuration.UserAccountDetailsImpl;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.repositories.AccountRepository;
import ru.inner.project.MyJAAS.repositories.UserRoleRepository;

@Service
public class UserAccountDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserAccountDetailsServiceImpl(AccountRepository accountRepository, UserRoleRepository userRoleRepository) {
        this.accountRepository = accountRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account userAccount = accountRepository.findAccountByLogin(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("Not found userAccount with login: " + username);
        }
        return new UserAccountDetailsImpl(userAccount, userRoleRepository);
    }

    public Account getUserAccountByLogin(String userLogin) {
        return accountRepository.findAccountByLogin(userLogin);
    }

}
