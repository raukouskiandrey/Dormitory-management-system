package com.example.project.mapper;

import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    List<StudentResponseDto> toDtoList(List<Student> students);

    @Mapping(target = "violationIds", expression = "java(mapViolations(student))")
    StudentResponseDto toDto(Student student);

    Student toEntity(StudentRequestDto request);

    default List<Long> mapViolations(Student student) {
        if (student.getViolations() == null) {
            return Collections.emptyList();
        }
        return student.getViolations().stream()
                .map(violation -> violation.getId())
                .collect(Collectors.toList());
    }
}