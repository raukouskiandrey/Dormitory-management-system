package com.example.project.controller;

import com.example.project.dto.StudentDto;
import com.example.project.service.StudentService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/room/{number}")
    public List<StudentDto> getStudentsByRoom(@PathVariable int number) {
        return studentService.findStudentsByRoom(number);
    }

    @GetMapping("/age")
    public List<StudentDto> getStudentsByAge(@RequestParam int age) {
        return studentService.findStudentsByAge(age);
    }
}