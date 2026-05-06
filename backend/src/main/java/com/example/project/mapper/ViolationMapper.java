package com.example.project.mapper;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.model.Violation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ViolationMapper {
    List<ViolationResponseDto> toDtoList(List<Violation> violations);

    @Mapping(target = "studentIds", expression = "java(violation.getStudents().stream()"
            + ".map(student -> student.getId())"
            + ".collect(java.util.stream.Collectors.toList()))")
    ViolationResponseDto toDto(Violation violation);

    Violation toEntity(ViolationRequestDto request);
}