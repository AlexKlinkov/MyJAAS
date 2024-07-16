package ru.inner.project.MyJAAS.inputDTO.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnBlockUserInputDTO {
    private String userEmailForUnBlock;
    private String commentOfUnBlocking;
}
