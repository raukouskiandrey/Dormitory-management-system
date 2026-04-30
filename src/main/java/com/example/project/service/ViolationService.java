package com.example.project.service;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.ViolationMapper;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import com.example.project.model.enums.ViolationType;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ViolationService {
    public final ViolationMapper violationMapper;
    public final ViolationRepository violationRepository;
    private final StudentRepository studentRepository;
    private static final String VIOLATION_NOT_FOUND = "Нарушение с id не найдено:";

    public ViolationService(ViolationRepository violationRepository,
                            ViolationMapper violationMapper,
                            StudentRepository studentRepository) {
        this.violationRepository = violationRepository;
        this.violationMapper = violationMapper;
        this.studentRepository = studentRepository;
    }

    public List<ViolationResponseDto> findViolations() {
        List<Violation> violations = violationRepository.findAllWithStudents();
        return violationMapper.toDtoList(violations);
    }

    /**
     * Поиск с фильтрами:
     * @param violationType - фильтр по типу нарушения (null = все)
     * @param fio           - поиск по ФИО студента (null или "" = все)
     * @param sortByDate    - "asc" = старые первые, "desc" = новые первые
     */
    public List<ViolationResponseDto> findViolationsFiltered(
            ViolationType violationType,
            String fio,
            String sortByDate
    ) {
        List<Violation> violations = violationRepository.findAllWithFilters(violationType, fio);

        // Сортировка на Java-уровне, чтобы надёжно работало с DISTINCT
        Comparator<Violation> comparator = Comparator.comparing(
                v -> LocalDate.parse(v.getDate())
        );

        if ("asc".equalsIgnoreCase(sortByDate)) {
            violations.sort(comparator); // старые первые
        } else {
            violations.sort(comparator.reversed()); // новые первые (по умолчанию)
        }

        return violationMapper.toDtoList(violations);
    }

    public Violation findViolationById(Long id) {
        return violationRepository.findViolationById(id)
                .orElseThrow(() -> new ResourceNotFoundException(VIOLATION_NOT_FOUND + id));
    }

    public ViolationResponseDto createViolation(Long studentId, ViolationRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студент с id не найден: " + studentId));

        validateViolationDate(request.getDate());

        Violation violation = violationMapper.toEntity(request);

        if (violation.getStudents() == null) {
            violation.setStudents(new java.util.HashSet<>());
        }

        if (student.getViolations() == null) {
            student.setViolations(new java.util.HashSet<>());
        }

        student.getViolations().add(violation);
        violation.getStudents().add(student);

        violationRepository.save(violation);
        studentRepository.save(student);

        return violationMapper.toDto(violation);
    }

    public ViolationResponseDto updateViolation(Long id, ViolationRequestDto updatedViolation) {
        Violation violation = violationRepository.findViolationById(id)
                .orElseThrow(() -> new ResourceNotFoundException(VIOLATION_NOT_FOUND + id));

        validateViolationDate(updatedViolation.getDate());
        violation.setDate(updatedViolation.getDate());
        violation.setViolationType(updatedViolation.getViolationType());
        violationRepository.save(violation);
        return violationMapper.toDto(violation);
    }

    public ViolationResponseDto updatePatchViolation(Long id, ViolationRequestDto updatedViolation) {
        Violation violation = violationRepository.findViolationById(id)
                .orElseThrow(() -> new ResourceNotFoundException(VIOLATION_NOT_FOUND + id));

        if (updatedViolation.getDate() != null) {
            validateViolationDate(updatedViolation.getDate());
            violation.setDate(updatedViolation.getDate());
        }

        if (updatedViolation.getViolationType() != null) {
            violation.setViolationType(updatedViolation.getViolationType());
        }

        violationRepository.save(violation);
        return violationMapper.toDto(violation);
    }

    public void deleteViolationById(Long id) {
        Violation violation = violationRepository.findViolationById(id)
                .orElseThrow(() -> new ResourceNotFoundException(VIOLATION_NOT_FOUND + id));
        for (Student student : violation.getStudents()) {
            student.getViolations().remove(violation);
        }
        violationRepository.delete(violation);
    }

    private void validateViolationDate(String dateStr) {
        LocalDate violationDate = LocalDate.parse(dateStr);
        if (violationDate.isAfter(LocalDate.now())) {
            throw new BadRequestException("Дата нарушения не может быть в будущем");
        }
    }
}