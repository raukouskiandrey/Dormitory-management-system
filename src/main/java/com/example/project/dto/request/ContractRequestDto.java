package com.example.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание или обновление договора")
public class ContractRequestDto {
    @Schema(description = "Уникальный номер договора", example = "100500")
    @NotNull(message = "Номер договора обязателен")
    @Min(value = 1, message = "Некорректный номер договора")
    private Integer number;

    @Schema(description = "Дата вступления договора в силу", example = "2024-09-01")
    @NotBlank(message = "Дата начала обязательна")
    private String startDate;

    @Schema(description = "Дата истечения срока действия договора", example = "2025-06-30")
    @NotBlank(message = "Дата окончания обязательна")
    private String endDate;
}