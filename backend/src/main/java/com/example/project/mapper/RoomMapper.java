package com.example.project.mapper;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.model.Room;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface RoomMapper {
    List<RoomResponseDto> toDtoList(List<Room> rooms);

    RoomResponseDto toDto(Room room);

    Room toEntity(RoomRequestDto request);
}