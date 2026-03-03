package com.example.project.service;

import com.example.project.dto.StudentDto;
import com.example.project.mapper.StudentMapper;
import com.example.project.model.Student;
import com.example.project.repository.StudentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    public List<StudentDto> findStudentsByRoom(int number) {
        List<Student> students = studentRepository.findStudentsByRoom(number);
        return studentMapper.toDtoList(students);
    }

    public List<StudentDto> findStudentsByAge(int age) {
        List<Student> students = studentRepository.findStudentsByAge(age);
        return studentMapper.toDtoList(students);
    }
}
