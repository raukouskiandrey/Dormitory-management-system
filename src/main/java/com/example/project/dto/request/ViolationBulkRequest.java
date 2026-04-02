package com.example.project.dto.request;

import com.example.project.model.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ViolationBulkRequest {
    private Long studentId;
    private String date;
    private ViolationType type;
}
