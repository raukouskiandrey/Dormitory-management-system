package com.example.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Данные для обновления информации о студенте")
public class StudentRequestDto {
    @Schema(description = "Имя студента", example = "Иван")
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Schema(description = "Фамилия студента", example = "Иванов")
    @NotBlank(message = "Фамилия не должна быть пустой")
    private String surname;

    @Schema(description = "Отчество студента (при наличии)", example = "Иванович")
    private String patronymic;

    @Schema(description = "Контактный номер телефона", example = "+79991234567")
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    @Schema(description = "Возраст студента", example = "20")
    @NotNull(message = "Возраст обязателен")
    @Min(value = 16, message = "Студент должен быть старше 16 лет")
    @Max(value = 100, message = "Некорректный возраст")
    private Integer age;

    @Schema(description = "Часы общественно-полезного труда (ОПТ)", example = "5")
    @Min(value = 0, message = "часы ОПТ не могут быть отрицательными")
    private Integer chs;
}