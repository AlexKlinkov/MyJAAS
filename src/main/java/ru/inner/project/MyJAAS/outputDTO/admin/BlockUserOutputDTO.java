package ru.inner.project.MyJAAS.outputDTO.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockUserOutputDTO {
    private String commentOfBlocking;
}
