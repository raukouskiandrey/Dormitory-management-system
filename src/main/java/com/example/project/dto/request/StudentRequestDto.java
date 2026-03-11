package com.example.project.dto.request;

import lombok.Data;

@Data
public class StudentRequestDto {
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private Integer age;
    private Integer chs;
}