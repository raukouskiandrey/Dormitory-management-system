package com.example.project;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.model.Student;
import com.example.project.model.ViolationType;
import com.example.project.model.Violation;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.StudentService;
import com.example.project.mapper.StudentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCoverageSupplementTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ViolationRepository violationRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Массовое назначение нарушений — успех")
    void assignViolations_Success() {
        // Создаем DTO, используя обновленный ViolationRequestDto
        ViolationRequestDto req = ViolationRequestDto.builder()
                .studentId(1L)
                .date("2024-01-01")
                .violationType(ViolationType.SMOKING)
                .build();

        Student student = Student.builder()
                .id(1L)
                .violations(new HashSet<>())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.assignViolationsToStudentsWithTx(List.of(req));

        verify(violationRepository, times(1)).save(any(Violation.class));
        verify(studentRepository, times(1)).save(student);
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Массовое назначение нарушений — пустой список")
    void assignViolations_EmptyList() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            studentService.assignViolationsToStudentsNoTx(List.of());
        });
    }
}