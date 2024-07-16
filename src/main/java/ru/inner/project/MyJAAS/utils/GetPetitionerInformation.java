package ru.inner.project.MyJAAS.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.inner.project.MyJAAS.repositories.AccountRepository;

import java.util.Optional;

@Component
public class GetPetitionerInformation { // The class take info about user from security context
    @Autowired
    private AccountRepository userAccountRepository;
    public String getPetitionerLoginFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        return Optional.ofNullable(principal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername)
                .orElse("");
    }

    public String getUserSubscriptionWhoMakesQuery() {
        String login = getPetitionerLoginFromContext();
        return userAccountRepository.findAccountByLogin(login).getSubscription();

    }
}
