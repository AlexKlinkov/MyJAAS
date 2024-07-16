package ru.inner.project.MyJAAS.outputDTO.register;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationOutputDTO {
    private String jwt_token;
}
