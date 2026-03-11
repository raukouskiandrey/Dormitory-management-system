package com.example.project.dto.response;

import com.example.project.model.ViolationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ViolationResponseDto {
    Long id;
    private ViolationType violationType;
    private String date;
    private List<Long> studentIds;
}
