package ru.inner.project.MyJAAS.outputDTO.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOutputDTO {
    private String username;
    private String email;
    private Integer age;
}
