package com.example.project.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DormitoryResponseDto {
    Long id;
    private String name;
    private String address;
    private List<RoomResponseDto> rooms;
}
