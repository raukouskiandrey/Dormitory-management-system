package com.example.project.dto.request;

import com.example.project.model.ViolationType;
import lombok.Data;

@Data
public class ViolationRequestDto {
    private ViolationType violationType;
    private String date;
}