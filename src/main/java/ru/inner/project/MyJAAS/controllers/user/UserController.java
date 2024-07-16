package ru.inner.project.MyJAAS.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.inner.project.MyJAAS.entities.User;
import ru.inner.project.MyJAAS.inputDTO.account.UpdateUserLoginAndPasswordInfoInputDTO;
import ru.inner.project.MyJAAS.inputDTO.user.UserInputDTO;
import ru.inner.project.MyJAAS.outputDTO.user.UserOutputDTO;
import ru.inner.project.MyJAAS.services.user.UserServiceImpl;

@Controller
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/api/v1/user/info")
    public ResponseEntity<User> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PutMapping("/api/v1/user/update")
    public ResponseEntity<UserOutputDTO> updateUserInformation(@RequestBody UserInputDTO userInputDTO) {
        return ResponseEntity.ok(userService.updateUserInformation(userInputDTO));
    }

    @DeleteMapping("/api/v1/user/delete")
    public ResponseEntity<?> deleteUserInformation() {
        return ResponseEntity.ok(userService.deleteUser());
    }

    // This controller return a form for update user Login and Password (account information)
    @GetMapping("/api/v1/user/account/update/loginandpassword")
    public String getUpdateForm(Model model) {
        model.addAttribute("updateUserLoginAndPassword", new UpdateUserLoginAndPasswordInfoInputDTO());
        return "updateUserLoginAndPassword";
    }
    // This controller in charge of handling the form for update user Login and Password (account information)
    @PostMapping("/api/v1/user/account/perform-update")
    public ResponseEntity<?> updateUserLoginAndPassword(
            @ModelAttribute UpdateUserLoginAndPasswordInfoInputDTO inputDTO) {

        return ResponseEntity.ok(userService.updateUserLoginAndPassword(inputDTO));
    }

    @GetMapping("/user/account/locked") // This controller return a message that user account is locked
    public ResponseEntity<?> getMessageUserAccountWasLocked() {
        return ResponseEntity.ok("Your account was locked");
    }
}
