package com.example.project.controller;

import com.example.project.dto.request.StudentCreationDto;
import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.ViolationType;
import com.example.project.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentResponseDto>> getStudents() {
        return ok(studentService.findStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentsById(@PathVariable Long id) {
        return ok(studentService.findStudentsById(id));
    }

    @GetMapping("/room/{number}")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByRoom(@PathVariable int number) {
        return ok(studentService.findStudentsByRoom(number));
    }

    @GetMapping("")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByAge(@RequestParam int age) {
        return ok(studentService.findStudentsByAge(age));
    }

    @GetMapping("/violation/{type}")
    public ResponseEntity<List<StudentResponseDto>> getStudentsByViolation(
            @PathVariable ViolationType type) {
        return ok(studentService.findByViolationsViolationType(type));
    }

    @PostMapping("/{studentId}/assign-to-room/{roomId}")
    public ResponseEntity<StudentResponseDto> assignStudentToRoom(
            @PathVariable Long studentId,
            @PathVariable Long roomId) {
        StudentResponseDto updatedStudent = studentService.assignStudentToRoom(studentId, roomId);
        return ok(updatedStudent);
    }

    @PostMapping("/{studentId}/add-violation/{violationId}")
    public ResponseEntity<StudentResponseDto> addViolationToStudent(
            @PathVariable Long studentId,
            @PathVariable Long violationId) {
        StudentResponseDto updatedStudent = studentService.addViolationToStudent(studentId, violationId);
        return ok(updatedStudent);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<StudentResponseDto> createStudent(
            @PathVariable Long roomId,
            @RequestBody StudentRequestDto student) {
        StudentResponseDto newStudent = studentService.createStudent(roomId, student);
        return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudentById(
            @PathVariable Long id,
            @RequestBody StudentRequestDto student) {
        return ok(studentService.updateStudent(id, student));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updatePatchStudentById(
            @PathVariable Long id,
            @RequestBody StudentRequestDto student) {
        return ok(studentService.updatePatchStudent(id, student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/noTx")
    public ResponseEntity<StudentResponseDto> createStudentWoTx(
            @RequestBody StudentCreationDto student) {
        try {
            StudentResponseDto newStudent = studentService.creationStudentNoTx(student);
            return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/withTx")
    public ResponseEntity<StudentResponseDto> createStudentWithTx(
            @RequestBody StudentCreationDto student) {
        try {
            StudentResponseDto newStudent = studentService.creationStudentWithTx(student);
            return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<StudentResponseDto>> filterStudentsPaged(
            @RequestParam(required = false) Integer chs,
            @RequestParam(required = false) ViolationType violationType,
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(studentService.filterStudentsWithJpqlPaged(chs, violationType, page, size));
    }

    @GetMapping("/filter/native")
    public ResponseEntity<Page<StudentResponseDto>> filterNativeStudentsPaged(
            @RequestParam(required = false) Integer chs,
            @RequestParam(required = false) String violationType,
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(studentService.filterStudentsWithNativePaged(chs, violationType, page, size));
    }

}