package com.example.project.mapper;

import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    List<StudentResponseDto> toDtoList(List<Student> students);

    @Mapping(target = "violationIds", expression = "java(mapViolationIds(student.getViolations()))")
    @Mapping(target = "roomNumber", source = "room.number")
    @Mapping(target = "dormitoryId", source = "room.dormitory.id")
    StudentResponseDto toDto(Student student);

    Student toEntity(StudentRequestDto request);

    default List<Long> mapViolationIds(java.util.Set<Violation> violations) {
        if (violations == null || violations.isEmpty()) {
            return Collections.emptyList();
        }

        return violations.stream()
                .map(Violation::getId)
                .collect(Collectors.toList());
    }
}