package com.example.project.dto.request;

import com.example.project.model.enums.ViolationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные о зафиксированном нарушении")
public class ViolationRequestDto {

    @Schema(description = "ID студента ", example = "1")
    private Long studentId;

    @Schema(description = "Тип нарушения согласно классификатору")
    @NotNull(message = "Тип нарушения обязателен")
    private ViolationType violationType;

    @Schema(description = "Дата и время инцидента", example = "2026-03-31")
    @NotBlank(message = "Дата нарушения обязательна")
    private String date;
}