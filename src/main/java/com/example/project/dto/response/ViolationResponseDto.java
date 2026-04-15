package com.example.project.dto.response;

import com.example.project.model.enums.ViolationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Информация о нарушении")
public class ViolationResponseDto {
    @Schema(description = "ID нарушения", example = "15")
    Long id;

    @Schema(description = "Тип нарушения")
    private ViolationType violationType;

    @Schema(description = "Дата нарушения", example = "2026-03-31")
    private String date;

    @Schema(description = "Список ID студентов, причастных к нарушению", example = "[1, 2, 5]")
    private List<Long> studentIds;
}
