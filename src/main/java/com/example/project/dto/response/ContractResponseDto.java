package com.example.project.dto.response;

import com.example.project.model.Student;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContractResponseDto {
    Long id;
    private Integer number;
    private String startDate;
    private String endDate;
    private StudentResponseDto student;
}
