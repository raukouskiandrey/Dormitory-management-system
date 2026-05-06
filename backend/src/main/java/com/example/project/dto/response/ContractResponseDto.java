package com.example.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Данные договора найма")
public class ContractResponseDto {
    @Schema(description = "ID договора", example = "50")
    Long id;

    @Schema(description = "Номер договора", example = "2024001")
    private Integer number;

    @Schema(description = "Дата начала действия", example = "2024-09-01")
    private String startDate;

    @Schema(description = "Дата окончания действия", example = "2025-06-30")
    private String endDate;

    @Schema(description = "Студент, с которым заключен договор")
    private StudentResponseDto student;
}
