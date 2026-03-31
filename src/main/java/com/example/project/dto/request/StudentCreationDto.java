package com.example.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Данные для регистрации нового студента вместе с контрактом и заселением")
public class StudentCreationDto {
    @Schema(description = "Имя студента", example = "Иван")
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Schema(description = "Фамилия студента", example = "Иванов")
    @NotBlank(message = "Фамилия не должна быть пустой")
    private String surname;

    @Schema(description = "Отчество студента", example = "Иванович")
    private String patronymic;

    @Schema(description = "Контактный номер телефона", example = "+79991234567")
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Неверный формат номера телефона")
    private String phoneNumber;

    @Schema(description = "Возраст студента", example = "19")
    @NotNull(message = "Возраст обязателен")
    @Min(value = 16, message = "Студент должен быть старше 16 лет")
    @Max(value = 100, message = "Некорректный возраст")
    private Integer age;

    @Schema(description = "Часы ОПТ", example = "10")
    @Min(value = 0, message = "часы ОПТ не могут быть отрицательными")
    private Integer chs;

    @Schema(description = "Номер создаваемого контракта", example = "2024001")
    @NotNull(message = "Номер контракта обязателен")
    private Integer contractNumber;

    @Schema(description = "Дата начала контракта", example = "2024-09-01")
    @NotBlank(message = "Дата начала контракта обязательна")
    private String contractStartDate;

    @Schema(description = "Дата окончания контракта", example = "2025-06-30")
    @NotBlank(message = "Дата окончания контракта обязательна")
    private String contractEndDate;

    @Schema(description = "ID комнаты для заселения", example = "10")
    @NotNull(message = "ID комнаты обязателен")
    private Long roomId;

    @Schema(description = "Техническое поле для инициации проблемы", example = "false")
    @NotBlank(message = "Есть ли ошибка?")
    private boolean initiateProblem;
}