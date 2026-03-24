package com.example.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {
    Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private Integer age;
    private Integer chs;
    private List<Long> violationIds;
    private Integer roomNumber;
    private Long dormitoryId;

    public StudentResponseDto(Long id, String name, String surname, String patronymic,
                                String phoneNumber, Integer age, Integer chs,
                              Integer roomNumber, Long dormitoryId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.chs = chs;
        this.roomNumber = roomNumber;
        this.dormitoryId = dormitoryId;
        this.violationIds = new ArrayList<>();
    }
}



