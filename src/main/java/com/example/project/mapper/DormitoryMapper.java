package com.example.project.mapper;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.model.Dormitory;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RoomMapper.class})
public interface DormitoryMapper {
    public List<DormitoryResponseDto> toDtoList(List<Dormitory> dormitories);
    public DormitoryResponseDto toDto(Dormitory dormitory);
    public Dormitory toEntity(DormitoryRequestDto request);
}
