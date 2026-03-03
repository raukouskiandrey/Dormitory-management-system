package com.example.project.mapper;

import com.example.project.dto.StudentDto;
import com.example.project.model.Student;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {
    public List<StudentDto> toDtoList(List<Student> students) {
        List<StudentDto> studentsDto = new ArrayList<>();
        for (Student student : students) {
            StudentDto studentDto = toDto(student);
            studentsDto.add(studentDto);
        }
        return studentsDto;
    }

    public StudentDto toDto(Student student) {
        StudentDto studentDto = new StudentDto();
        studentDto.setName(student.getName());
        studentDto.setSurname(student.getSurname());
        studentDto.setPatronymic(student.getPatronymic());
        return studentDto;
    }
}
