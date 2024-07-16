package ru.inner.project.MyJAAS.outputDTO.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllInfoAboutUserOutputDTO {
    private Long userId;
    private String userName;
    private Integer userAge;
    private String userEmail;
    private String userLogin;
    private String userSubscription;
    private Set<String> userRole;
    private Boolean isUserAccountLocked;
}
