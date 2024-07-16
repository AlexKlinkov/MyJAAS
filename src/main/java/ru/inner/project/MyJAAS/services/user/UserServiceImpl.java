package ru.inner.project.MyJAAS.services.user;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.entities.User;
import ru.inner.project.MyJAAS.inputDTO.account.UpdateUserLoginAndPasswordInfoInputDTO;
import ru.inner.project.MyJAAS.inputDTO.register.UserAccountRegistrationInputDTO;
import ru.inner.project.MyJAAS.inputDTO.user.UserInputDTO;
import ru.inner.project.MyJAAS.outputDTO.user.UserOutputDTO;
import ru.inner.project.MyJAAS.repositories.AccountRepository;
import ru.inner.project.MyJAAS.repositories.UserRepository;
import ru.inner.project.MyJAAS.repositories.UserRoleRepository;
import ru.inner.project.MyJAAS.services.RegisterServiceImpl;
import ru.inner.project.MyJAAS.utils.GetPetitionerInformation;
import ru.inner.project.MyJAAS.utils.ValidateObject;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl {

    private UserRepository userRepository;

    private AccountRepository userAccountRepository;

    private GetPetitionerInformation getPetitionerInformation;

    private RegisterServiceImpl registerService;

    private UserRoleRepository userRoleRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setGetPetitionerInformation(GetPetitionerInformation getPetitionerInformation) {
        this.getPetitionerInformation = getPetitionerInformation;
    }

    @Autowired
    public void setUserAccountRepository(AccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Autowired
    public void setRegisterService(RegisterServiceImpl registerService) {
        this.registerService = registerService;
    }

    @Autowired
    public void setUserRoleRepository(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public User getUserInfo() {
        log.debug("getUserInfo method in UserServiceImpl was started at " + LocalDateTime.now());
        User userFromBD = getUserInfoFromBDWhoMakesQuery();
        if (userFromBD == null) {
            throw new RuntimeException("User's information wasn't found in BD, at " + LocalDateTime.now());
        }
        log.debug("getUserInfo method in UserServiceImpl is returning the user information - " + userFromBD);
        return userFromBD;
    }

    public UserOutputDTO updateUserInformation(UserInputDTO userInputDTO) {
        log.debug("updateUserInformation method in UserServiceImpl was started at " + LocalDateTime.now());
        User userFromBD = getUserInfoFromBDWhoMakesQuery();
        if (userFromBD == null) {
            throw new RuntimeException("User wasn't found in BD to update, at " + LocalDateTime.now());
        }
        // NEW user fields
        String newUserName = userInputDTO.getUsername();
        String newUserEmail = userInputDTO.getEmail();
        Integer newUserAge = userInputDTO.getAge();
        // update username
        if (ValidateObject.objectIsNotNullOrEmpty(newUserName)) {
            if (newUserName.length() >= 2 && newUserName.length() <= 100) {
                userFromBD.setUsername(newUserName);
            }
        }
        // update user email
        if (ValidateObject.objectIsNotNullOrEmpty(newUserEmail)) {
            if (newUserEmail.contains("@") && newUserEmail.contains(".")) {
                userFromBD.setEmail(newUserEmail);
            }
        }
        // update user age
        if (newUserAge != null) {
            if (newUserAge > 0 && newUserAge <= 100) {
                userFromBD.setAge(newUserAge);
            }
        }
        userRepository.save(userFromBD); // write updated user again in BD
        log.debug("updateUserInformation method in " +
                "UserServiceImpl is returning updated user information - " + userFromBD);
        return new UserOutputDTO(newUserName, newUserEmail, newUserAge);
    }

    public String deleteUser() {
        log.debug("deleteUser method in UserServiceImpl was started at " + LocalDateTime.now());
        User userFromBD = getUserInfoFromBDWhoMakesQuery();
        if (userFromBD == null) {
            throw new RuntimeException("User wasn't found in BD to delete, at " + LocalDateTime.now());
        }
        userRepository.delete(userFromBD);
        log.debug("deleteUser method in UserServiceImpl was successful accomplished for user: " + userFromBD);
        return "You was deleted successfully :(";
    }

    @Transactional
    public String updateUserLoginAndPassword(UpdateUserLoginAndPasswordInfoInputDTO inputDTO) {
        log.debug("updateUserLoginAndPassword method in UserServiceImpl was started at " + LocalDateTime.now());
        User userFromBD = getUserInfoFromBDWhoMakesQuery();
        if (userFromBD == null) {
            throw new RuntimeException("User wasn't found in BD to update Login " +
                    "and password, at " + LocalDateTime.now());
        }
        if (userAccountRepository.findAccountByLogin(inputDTO.getLogin()) != null) {
            return "This login is already used";
        }
        if (!inputDTO.getPassword().equals(inputDTO.getRepeatPassword())) {
            return "Your passwords didn't coincide";
        }
        // register again old user, coz it is impossible to update entity, right away, where we want update primary key,
        // also it is necessary to get a valid jwt token for user
        Account userAccountFromBD = userAccountRepository.findAccountByLogin(
                getPetitionerInformation.getPetitionerLoginFromContext());
        String initialEmail = userFromBD.getEmail();
        userFromBD.setEmail("Intermediate@email.ru"); // necessary because registerService will check unique fields
        userRepository.saveAndFlush(userFromBD); // update bd
        userAccountRepository.delete(userAccountFromBD);
        userAccountRepository.flush(); // force update state of BD

        registerService.register(new UserAccountRegistrationInputDTO(
           userFromBD.getUsername(), userFromBD.getAge(), initialEmail, inputDTO.getLogin(),
                inputDTO.getPassword(), inputDTO.getRepeatPassword())
        );

        Account userAccountFromBDAfterUpdated = userAccountRepository.findAccountByLogin(inputDTO.getLogin());
        userAccountFromBDAfterUpdated.setSubscription(userAccountFromBD.getSubscription());
        userAccountFromBDAfterUpdated.setIsLockedAccount(userAccountFromBD.getIsLockedAccount());
        userAccountFromBDAfterUpdated.setIsLoggedOut(userAccountFromBD.getIsLoggedOut());
        userAccountRepository.save(userAccountFromBDAfterUpdated);
        userRepository.delete(userRepository.findUserByEmail("Intermediate@email.ru"));
        userRoleRepository.deleteAll(userRoleRepository.getUserRolesByUserId(userFromBD.getUserId()));
        return "Now your login is " + inputDTO.getLogin() + " your password was updated";
    }

    // Auxiliary methods
    public boolean isUserInBd() {
        return userAccountRepository.findAccountByLogin(
                getPetitionerInformation.getPetitionerLoginFromContext()) != null;
    }

    private User getUserInfoFromBDWhoMakesQuery() {
        String login = getPetitionerInformation.getPetitionerLoginFromContext();
        Long userId = userAccountRepository.findAccountByLogin(login).getUserId();
        Optional<User> userFromBD = userRepository.findById(userId);
        return userFromBD.orElse(null);
    }

}
