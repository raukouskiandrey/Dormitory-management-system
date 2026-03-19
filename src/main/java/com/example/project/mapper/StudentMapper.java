package com.example.project.mapper;

import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    List<StudentResponseDto> toDtoList(List<Student> students);

    @Mapping(target = "violationIds", expression = "java(mapViolations(student.getViolations()))")
    StudentResponseDto toDto(Student student);

    Student toEntity(StudentRequestDto request);

    // ИСПРАВЛЕНИЕ: возвращаем Long, чтобы соответствовать List<Long> в DTO
    default List<Long> mapViolations(java.util.Collection<Violation> violations) {
        if (violations == null) {
            return java.util.Collections.emptyList();
        }
        return violations.stream()
                .map(v -> v.getId()) // Берем ID нарушения (Long), а не его тип (String)
                .collect(java.util.stream.Collectors.toList());
    }
}

