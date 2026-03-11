package com.example.project.service;


import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.mapper.ViolationMapper;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationService {
    public ViolationMapper violationMapper;
    public ViolationRepository violationRepository;
    private final StudentService studentService;

    public ViolationService(ViolationRepository violationRepository,
                            ViolationMapper violationMapper,
                            StudentService studentService,
                            StudentRepository studentRepository) {
        this.violationRepository = violationRepository;
        this.violationMapper = violationMapper;
        this.studentService = studentService;
    }

    public List<ViolationResponseDto> findViolations() {
        List<Violation> violations = violationRepository.findAll();
        return violationMapper.toDtoList(violations);
    }

    public ViolationResponseDto createViolation(Long studentId, ViolationRequestDto request) {
        Student student = studentService.findStudentEntityById(studentId);
        Violation violation = violationMapper.toEntity(request);

        student.getViolations().add(violation);
        violation.getStudents().add(student);
        violationRepository.save(violation);
        return violationMapper.toDto(violation);
    }

    public ViolationResponseDto updateViolation(Long id, ViolationRequestDto updatedViolation) {
        Violation violation = violationRepository.findViolationById(id);

        violation.setDate(updatedViolation.getDate());
        violation.setViolationType(updatedViolation.getViolationType());
        violationRepository.save(violation);
        return violationMapper.toDto(violation);
    }

    public ViolationResponseDto updatePatchViolation(Long id, ViolationRequestDto updatedViolation) {
        Violation violation = violationRepository.findViolationById(id);

        if (updatedViolation.getDate() != null) {
            violation.setDate(updatedViolation.getDate());
        }

        if (updatedViolation.getViolationType() != null) {
            violation.setViolationType(updatedViolation.getViolationType());
        }

        violationRepository.save(violation);
        return violationMapper.toDto(violation);
    }

    public void deleteViolationById(Long id) {
        Violation violation = violationRepository.findViolationById(id);
        for (Student student : violation.getStudents()) {
            student.getViolations().remove(violation);
        }
        violationRepository.delete(violation);
    }
}
