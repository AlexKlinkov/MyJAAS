package ru.inner.project.MyJAAS.controllers.admin;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inner.project.MyJAAS.inputDTO.admin.BlockUserInputDTO;
import ru.inner.project.MyJAAS.inputDTO.admin.UnBlockUserInputDTO;
import ru.inner.project.MyJAAS.outputDTO.admin.AllInfoAboutUserOutputDTO;
import ru.inner.project.MyJAAS.outputDTO.admin.BlockUserOutputDTO;
import ru.inner.project.MyJAAS.outputDTO.admin.UnBlockUserOutputDTO;
import ru.inner.project.MyJAAS.services.admin.AdminService;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users/info")
    public ResponseEntity<Set<AllInfoAboutUserOutputDTO>> getUsersInformation() {
        return ResponseEntity.ok(adminService.getUsersInformation());
    }

    @GetMapping("/user/info/{userId}")
    public ResponseEntity<AllInfoAboutUserOutputDTO> getUserInformationByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserInformationByUserId(userId));
    }

    @PutMapping("/user/lockuseraccount")
    public ResponseEntity<BlockUserOutputDTO> lockUserAccountByUserEmail(
            @RequestBody BlockUserInputDTO blockUserInputDTO) {
        return ResponseEntity.ok(adminService.lockUserAccountByUserEmail(blockUserInputDTO));
    }

    @PutMapping("/user/unlockuseraccount")
    public ResponseEntity<UnBlockUserOutputDTO> unlockUserAccountByUserEmail(
            @RequestBody UnBlockUserInputDTO unBlockUserInputDTO) {
        return ResponseEntity.ok(adminService.unLockUserAccountByUserEmail(unBlockUserInputDTO));
    }

    @DeleteMapping("/user/info/delete/{userId}")
    public ResponseEntity<String> deleteUserAccountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deleteUserById(userId));
    }

    @PatchMapping("/user/info/{userId}/update/on/subscription")
    public ResponseEntity<AllInfoAboutUserOutputDTO> updateUserSubscription(@PathVariable Long userId,
                                                                            @RequestParam String newSubscription) {

        return ResponseEntity.ok(adminService.updateUserSubscription(userId, newSubscription));
    }
}
