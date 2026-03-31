package com.example.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Параметры комнаты")
public class RoomRequestDto {
    @Schema(description = "Номер комнаты на этаже", example = "302")
    @NotNull(message = "Номер комнаты обязателен")
    @Min(value = 1, message = "Номер комнаты должен быть больше 0")
    private Integer number;

    @Schema(description = "Максимальное количество жильцов", example = "4")
    @NotNull(message = "Количество мест обязательно")
    @Min(value = 1, message = "Минимум 1 место")
    @Max(value = 6, message = "Максимум 6 мест")
    private Integer totalPlaces;
}