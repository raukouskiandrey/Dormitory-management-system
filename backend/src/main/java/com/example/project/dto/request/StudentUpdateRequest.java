package com.example.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Данные для заселения студентов в комнату")
public class StudentUpdateRequest {
    @Schema(description = "ID студентов, которых надо заселить в комнату", example = "1")
    Long id;
}
