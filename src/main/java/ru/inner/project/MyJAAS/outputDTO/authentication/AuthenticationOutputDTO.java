package ru.inner.project.MyJAAS.outputDTO.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationOutputDTO {
    private String jwt_token;
}
