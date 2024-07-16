package ru.inner.project.MyJAAS.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.inner.project.MyJAAS.configuration.UserAccountDetailsImpl;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.inputDTO.authentication.AuthenticationInputDTO;
import ru.inner.project.MyJAAS.outputDTO.authentication.AuthenticationOutputDTO;
import ru.inner.project.MyJAAS.repositories.AccountRepository;
import ru.inner.project.MyJAAS.repositories.UserRoleRepository;
import ru.inner.project.MyJAAS.utils.GenerateNewAndCheckAlreadyExistToken;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
// This service responsible for checking user's jwt token and returning relevant information.
public class AuthenticationServiceImpl {

    private final AccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final GenerateNewAndCheckAlreadyExistToken jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationOutputDTO authenticate(AuthenticationInputDTO request) {
        log.debug("Authenticate method was launched at " + LocalDateTime.now());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        Account userAccount = userAccountRepository.findAccountByLogin(request.getLogin());
        userAccount.setIsLoggedOut(false); // put value 'false' in field 'is_Logged_Out'
        userAccountRepository.save(userAccount); // update info in BD

        String jwtToken = jwtService.generateToken(new UserAccountDetailsImpl(userAccount, userRoleRepository));
        log.debug("Authenticate method is going to return user token, time is " + LocalDateTime.now());
        return AuthenticationOutputDTO.builder()
                .jwt_token(jwtToken)
                .build();
    }

    public AccountRepository getUserAccountRepository() {
        return userAccountRepository;
    }
}
