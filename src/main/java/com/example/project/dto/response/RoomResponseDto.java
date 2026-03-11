package com.example.project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RoomResponseDto {
    Long id;
    private Integer number;
    private Integer totalPlaces;
    private List<StudentResponseDto> students;
}
