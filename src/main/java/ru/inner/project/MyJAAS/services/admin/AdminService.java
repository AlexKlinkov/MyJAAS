package ru.inner.project.MyJAAS.services.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.entities.User;
import ru.inner.project.MyJAAS.inputDTO.admin.UnBlockUserInputDTO;
import ru.inner.project.MyJAAS.outputDTO.admin.AllInfoAboutUserOutputDTO;
import ru.inner.project.MyJAAS.outputDTO.admin.UnBlockUserOutputDTO;
import ru.inner.project.MyJAAS.repositories.AccountRepository;
import ru.inner.project.MyJAAS.repositories.UserRepository;
import ru.inner.project.MyJAAS.inputDTO.admin.BlockUserInputDTO;
import ru.inner.project.MyJAAS.outputDTO.admin.BlockUserOutputDTO;
import ru.inner.project.MyJAAS.repositories.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminService {

    private UserRepository userRepository;

    private AccountRepository userAccountRepository;

    private UserRoleRepository userRoleRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserAccountRepository(AccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Autowired
    public void setUserRoleRepository(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public Set<AllInfoAboutUserOutputDTO> getUsersInformation() {
        log.debug("Start getUsersInformation method in AdminService starts, at " + LocalDateTime.now());
        Set<AllInfoAboutUserOutputDTO> usersInfo = new HashSet<>();
        List<User> users = userRepository.findAll(); // take users
        if (!users.isEmpty()) {
            for (User user : users) { // fill up all information about user
                usersInfo.add(createAllIUserInfo(user.getUserId(), user.getUsername(), user.getAge(), user.getEmail(),
                        userAccountRepository.findAccountByUserId(user.getUserId()).getLogin(),
                        userRoleRepository.getUserRolesByUserId(user.getUserId()).stream()
                                .map(x -> x.getId().getRole())
                                .collect(Collectors.toSet()),
                        userAccountRepository.findAccountByUserId(user.getUserId()).getSubscription(),
                        userAccountRepository.findAccountByUserId(user.getUserId()).getIsLockedAccount()));
            }
        }
        log.debug("getUsersInformation method in AdminService tries to return usersInfo: "
                + usersInfo + ", at " + LocalDateTime.now());
        return usersInfo;
    }

    public AllInfoAboutUserOutputDTO getUserInformationByUserId(Long userId) {
        log.debug("Start getUserInformationByUserId method in AdminService starts, at " + LocalDateTime.now());
        AllInfoAboutUserOutputDTO answer = new AllInfoAboutUserOutputDTO();
        answer.setUserName("User with ID " + userId + " not found");
        return getUsersInformation().stream()
                .filter(x -> x.getUserId().equals(userId))
                .findFirst()
                .orElse(answer);
    }

    public BlockUserOutputDTO lockUserAccountByUserEmail(BlockUserInputDTO blockUserInputDTO) {
        log.debug("Start method lockUserAccount, with next params: " + blockUserInputDTO.toString()
                + " " + LocalDateTime.now());
        User user = userRepository.findUserByEmail(blockUserInputDTO.getUserEmailForBlock());
        if (user != null) {
            // check, maybe user has already been blocked by admin
            // (by default all new users are active (this means that variable 'isLockedAccount' = false'))
            if (!userAccountRepository.findAccountByUserId(user.getUserId()).getIsLockedAccount()) {
                Account userAccount = userAccountRepository.findAccountByUserId(user.getUserId());
                userAccount.setIsLockedAccount(true);
                userAccountRepository.save(userAccount); // update status of account (put the block) in BD
            } else {
                throw new RuntimeException("User has already been blocked before");
            }
        }
        log.debug("Return information from lockUserAccount methods, with next param: "
                + blockUserInputDTO.getCommentOfBlocking() + " " + LocalDateTime.now());
        return BlockUserOutputDTO.builder()
                .commentOfBlocking(blockUserInputDTO.getCommentOfBlocking())
                .build();
    }

    public UnBlockUserOutputDTO unLockUserAccountByUserEmail(UnBlockUserInputDTO unBlockUserInputDTO) {
        log.debug("Start method unLockUserAccount, with next params: " + unBlockUserInputDTO.toString()
                + " " + LocalDateTime.now());
        User user = userRepository.findUserByEmail(unBlockUserInputDTO.getUserEmailForUnBlock());
        if (user != null) {
            // check, maybe user has already been unlocked by admin
            // (by default all new users are active (this means that variable 'isLockedAccount' = false'))
            if (userAccountRepository.findAccountByUserId(user.getUserId()).getIsLockedAccount()) {
                Account userAccount = userAccountRepository.findAccountByUserId(user.getUserId());
                userAccount.setIsLockedAccount(false);
                userAccountRepository.save(userAccount); // update status of account (unlocking)
            } else {
                throw new RuntimeException("User has already been unlocked before");
            }
        }
        log.debug("Return information from unLockUserAccount methods, with next param: "
                + unBlockUserInputDTO.getCommentOfUnBlocking() + " " + LocalDateTime.now());
        return UnBlockUserOutputDTO.builder()
                .commentOfUnBlocking(unBlockUserInputDTO.getCommentOfUnBlocking())
                .build();
    }

    public String deleteUserById(Long userId) {
        log.debug("Start deleteUserById method in AdminService starts, at " + LocalDateTime.now());
        User userFromBD = userRepository.findById(userId).orElse(null);
        if (userFromBD == null) {
            return "User with id: " + userId +
                    " wasn't found in system for deleting, at " + LocalDateTime.now();
        }
        log.debug("Return information from deleteUserById methods, with userId: " + userId + ", at " + LocalDateTime.now());
        userRepository.delete(userFromBD);
        return "User with id: " + userId + " was successfully deleted from system!";
    }

    public AllInfoAboutUserOutputDTO updateUserSubscription(Long userId, String newSubscription) {
        try {
            if (!checkSubscriptionExist(newSubscription)) { // check given subscription for update user account
                AllInfoAboutUserOutputDTO subscriptionDoesNotExist = new AllInfoAboutUserOutputDTO();
                subscriptionDoesNotExist.setUserSubscription("New subscription: " + newSubscription + " doesn't exist");
                return subscriptionDoesNotExist;
            }
            AllInfoAboutUserOutputDTO userFromBDForUpdate = getUserInformationByUserId(userId);
            userFromBDForUpdate.setUserSubscription(newSubscription.toUpperCase());
            // update info in BD (account table)
            Account userAccount = userAccountRepository.findAccountByUserId(userId);
            userAccount.setSubscription(newSubscription.toUpperCase());
            userAccountRepository.save(userAccount);
            return userFromBDForUpdate;
        } catch (NoSuchElementException ex) {
            AllInfoAboutUserOutputDTO userWithNotFoundException = new AllInfoAboutUserOutputDTO();
            userWithNotFoundException.setUserSubscription("User with userId: " + userId
                    + " wasn't found for subscription update");
            return userWithNotFoundException;
        }
    }

    // Auxiliary methods

    private AllInfoAboutUserOutputDTO createAllIUserInfo(Long userId, String userName, Integer userAge,
                                                         String userEmail, String userLogin, Set<String> userRoles,
                                                         String userSubscription, Boolean isUserAccountLocked) {
        AllInfoAboutUserOutputDTO outputDTO = new AllInfoAboutUserOutputDTO();
        outputDTO.setUserId(userId);
        outputDTO.setUserName(userName);
        outputDTO.setUserAge(userAge);
        outputDTO.setUserEmail(userEmail);
        outputDTO.setUserLogin(userLogin);
        outputDTO.setUserRole(userRoles);
        outputDTO.setUserSubscription(userSubscription);
        outputDTO.setIsUserAccountLocked(isUserAccountLocked);
        return outputDTO;
    }

    private boolean checkSubscriptionExist(String subscription) {
        Set<String> setExistenceSubscriptions = new HashSet<>();
        setExistenceSubscriptions.add("FREE");
        setExistenceSubscriptions.add("STANDARD");
        setExistenceSubscriptions.add("PREMIUM");
        return setExistenceSubscriptions.contains(subscription.toUpperCase());
    }
}
