package com.example.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Данные для создания/изменения общежития")
public class DormitoryRequestDto {
    @Schema(description = "Наименование", example = "Общежитие №4")
    @NotBlank(message = "Название общежития не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
    private String name;

    @Schema(description = "Фактический адрес", example = "ул. Университетская, д. 10")
    @NotBlank(message = "Адрес обязателен")
    private String address;
}