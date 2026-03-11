package com.example.project.mapper;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.model.Dormitory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoomMapper.class})
public interface DormitoryMapper {
    List<DormitoryResponseDto> toDtoList(List<Dormitory> dormitories);

    DormitoryResponseDto toDto(Dormitory dormitory);

    Dormitory toEntity(DormitoryRequestDto request);
}