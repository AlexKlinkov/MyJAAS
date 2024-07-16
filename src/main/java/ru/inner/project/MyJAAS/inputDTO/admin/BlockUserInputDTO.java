package ru.inner.project.MyJAAS.inputDTO.admin;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserInputDTO {
    private String userEmailForBlock;
    private String commentOfBlocking;
}
