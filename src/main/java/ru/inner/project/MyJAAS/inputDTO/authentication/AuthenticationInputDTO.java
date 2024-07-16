package ru.inner.project.MyJAAS.inputDTO.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationInputDTO {
    private String login;
    private String password;
}
