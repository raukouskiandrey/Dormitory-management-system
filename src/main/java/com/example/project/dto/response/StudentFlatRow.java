package com.example.project.dto.response;

public record StudentFlatRow(
        Long id,
        String name,
        String surname,
        String patronymic,
        String phoneNumber,
        Integer age,
        Integer chs,
        Integer roomNumber,
        Long dormitoryId,
        Long violationId
) {

}
