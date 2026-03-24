package com.example.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    Long id;
    String name;
    String surname;
    String patronymic;
    String phoneNumber;
    Integer age;
    Integer chs;
    Integer roomNumber;
    Long dormitoryId;
    String violationIds;
}




