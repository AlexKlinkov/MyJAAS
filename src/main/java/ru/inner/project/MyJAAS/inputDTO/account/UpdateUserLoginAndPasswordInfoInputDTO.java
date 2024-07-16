package ru.inner.project.MyJAAS.inputDTO.account;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserLoginAndPasswordInfoInputDTO {
    @Size(min = 2, max = 30, message = "Login should be unique")
    private String login;
    @Size(min = 6, max = 16, message = "Password should be between 6 till 16")
    private String password;
    @Size(min = 6, max = 16, message = "Second password should be the same like first one")
    private String repeatPassword;
}
