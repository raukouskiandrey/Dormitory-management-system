package com.example.project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentResponseDto {
    Long id;
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private Integer age;
    private Integer chs;
    private List<Long> violationIds;
}

