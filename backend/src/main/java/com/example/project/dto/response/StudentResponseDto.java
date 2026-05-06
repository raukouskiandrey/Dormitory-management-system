package com.example.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о студенте")
public class StudentResponseDto {
    @Schema(description = "ID студента", example = "1")
    Long id;

    @Schema(description = "Имя", example = "Иван")
    String name;

    @Schema(description = "Фамилия", example = "Иванов")
    String surname;

    @Schema(description = "Отчество", example = "Иванович")
    String patronymic;

    @Schema(description = "Номер телефона", example = "+79991234567")
    String phoneNumber;

    @Schema(description = "Возраст", example = "20")
    Integer age;

    @Schema(description = "Статус (например, 1 - активен, 0 - отчислен/в зоне риска)", example = "1")
    Integer chs;

    @Schema(description = "Номер комнаты", example = "302")
    Integer roomNumber;

    @Schema(description = "ID общежития", example = "2")
    Long dormitoryId;

    @Schema(description = "Список ID нарушений через запятую", example = "1,4,12")
    List<Long> violationIds;
}




