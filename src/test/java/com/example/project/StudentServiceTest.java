package com.example.project;

import com.example.project.dto.response.StudentResponseDto;
import com.example.project.dto.request.ViolationBulkRequest;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import com.example.project.model.ViolationType; // Добавили импорт Enum
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.StudentMapper;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ViolationRepository violationRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Массовое создание: успех для всех записей")
    void assignViolations_Success() {
        // Given
        Long id = 1L;
        Student student = Student.builder().id(id).violations(new HashSet<>()).build();

        // ИСПРАВЛЕНО: Передаем ViolationType.SMOKING вместо строки
        ViolationBulkRequest req = new ViolationBulkRequest(id, "2026-04-02", ViolationType.SMOKING);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        // When
        List<StudentResponseDto> result = studentService.assignViolationsToStudentsWithTx(List.of(req));

        // Then
        assertThat(result).hasSize(1);
        assertThat(student.getViolations()).hasSize(1);
        verify(violationRepository, times(1)).save(any(Violation.class));
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("Ошибка ResourceNotFoundException, если ID студента не существует")
    void assignViolations_NotFound() {
        // Given
        Long badId = 999L;
        // ИСПРАВЛЕНО: Передаем ViolationType.NOISE вместо строки
        ViolationBulkRequest req = new ViolationBulkRequest(badId, "2026-04-02", ViolationType.NOISE);

        when(studentRepository.findById(badId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.assignViolationsToStudentsWithTx(List.of(req)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Студент не найден");

        verify(violationRepository, never()).save(any());
    }
}