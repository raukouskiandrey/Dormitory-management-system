package com.example.project.dto.request;

import lombok.Data;

@Data
public class StudentCreationDto {

    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private Integer age;
    private Integer chs;

    private Integer contractNumber;
    private String contractStartDate;
    private String contractEndDate;

    private Long roomId;

    private boolean initiateProblem;
}