package ru.inner.project.MyJAAS.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.inner.project.MyJAAS.configuration.UserAccountDetailsImpl;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.entities.UserRole;
import ru.inner.project.MyJAAS.entities.UserRoleKey;
import ru.inner.project.MyJAAS.inputDTO.register.UserAccountRegistrationInputDTO;
import ru.inner.project.MyJAAS.outputDTO.register.UserRegistrationOutputDTO;
import ru.inner.project.MyJAAS.repositories.AccountRepository;
import ru.inner.project.MyJAAS.repositories.UserRoleRepository;
import ru.inner.project.MyJAAS.utils.GenerateNewAndCheckAlreadyExistToken;
import ru.inner.project.MyJAAS.entities.User;
import ru.inner.project.MyJAAS.repositories.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
// This service responsible for user's registering and saving information about him in BD.
public class RegisterServiceImpl {

    private final UserRepository userRepository;
    private final AccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenerateNewAndCheckAlreadyExistToken jwtService;
    @Transactional
    public UserRegistrationOutputDTO register(UserAccountRegistrationInputDTO request) {
        if (userAccountRepository.findAccountByLogin(request.getLogin()) != null) {
            return new UserRegistrationOutputDTO("This login is already used");
        }
        if (userRepository.findUserByEmail(request.getEmail()) != null) {
            return new UserRegistrationOutputDTO("This email is already used");
        }
        log.debug("Start method register with request - " + request + " time is " + LocalDateTime.now());
        Long userId = saveUserInBD(createUser(request));
        saveUserAccountInBD(createUserAccount(request, userId));
        saveUserRoleInBD(createUserRole(userId));
        String jwtToken = jwtService.generateToken(
                new UserAccountDetailsImpl(userAccountRepository.findAccountByLogin(request.getLogin()), userRoleRepository));
        log.debug("Finish method register, time is " + LocalDateTime.now());
        return UserRegistrationOutputDTO.builder()
                .jwt_token(jwtToken)
                .build();
    }

    private User createUser(UserAccountRegistrationInputDTO request) {
        log.debug("Start method createUser with request - " + request + " time is " + LocalDateTime.now());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setAge(request.getAge());
        user.setEmail(request.getEmail());
        log.debug("Finish method createUser, time is " + LocalDateTime.now());
        return user;
    }

    private Long saveUserInBD(User user) {
        log.debug("Start method saveUserInBD,  userInfo is - " + user + " time is " + LocalDateTime.now());
        return userRepository.saveAndFlush(user).getUserId();
    }

    private Account createUserAccount(UserAccountRegistrationInputDTO request, Long userId) {
        log.debug("Start method createUserAccount with request - " + request + " and userId - " + userId +
                " time is " + LocalDateTime.now());
        if (!request.getPassword().equals(request.getRepeatPassword())) {
            Account errorAccount = new Account();
            errorAccount.setPassword("Your passwords don't coincide");
        }
        Account userAccount = new Account();
        userAccount.setUserId(userId);
        userAccount.setLogin(request.getLogin());
        userAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        userAccount.setSubscription("FREE"); // Default value
        userAccount.setIsLockedAccount(false); // Default value
        userAccount.setIsLoggedOut(false); // Default value
        log.debug("Finish method createUserAccount, time is " + LocalDateTime.now());
        return userAccount;
    }

    private void saveUserAccountInBD(Account userAccount) {
        log.debug("Start method saveUserAccount,  userAccountInfo is - " + userAccount +
                " time is " + LocalDateTime.now());
        userAccountRepository.saveAndFlush(userAccount);
    }

    private UserRole createUserRole(Long userId) {
        log.debug("Start method createUserRole with userId - " + userId + " time is " + LocalDateTime.now());
        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleKey(userId, "USER")); // Default value
        log.debug("Finish method createUserRole, time is " + LocalDateTime.now());
        return userRole;
    }

    private void saveUserRoleInBD(UserRole userRole) {
        log.debug("Start method saveUserRole,  userRoleInfo is - " + userRole + " time is " + LocalDateTime.now());
        userRoleRepository.saveAndFlush(userRole);
    }
}