package com.example.project.mapper;

import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    public List<StudentResponseDto> toDtoList(List<Student> students);
    @Mapping(target = "violationIds",
            expression = "java(student.getViolations().stream().map(violation -> violation.getId()).collect(java.util.stream.Collectors.toList()))")
    public StudentResponseDto toDto(Student student);
    public Student toEntity(StudentRequestDto request);
}
