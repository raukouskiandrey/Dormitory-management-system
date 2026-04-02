package com.example.project;

import com.example.project.dto.response.StudentResponseDto;
import com.example.project.dto.request.ViolationBulkRequest;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import com.example.project.model.ViolationType;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.StudentMapper;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.StudentService;
import com.example.project.service.RoomService;
import com.example.project.service.ViolationService;
import com.example.project.cache.CacheManager;
import com.example.project.repository.ContractRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Mock
    private RoomService roomService;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ViolationService violationService;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Массовое создание: успех для всех записей")
    void assignViolations_Success() {
        Long id = 1L;
        Student student = Student.builder().id(id).violations(new HashSet<>()).build();

        ViolationBulkRequest req = new ViolationBulkRequest(id, "2026-04-02", ViolationType.SMOKING);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        List<StudentResponseDto> result = studentService.assignViolationsToStudentsWithTx(List.of(req));

        assertThat(result).hasSize(1);
        assertThat(student.getViolations()).hasSize(1);
        verify(violationRepository, times(1)).save(any(Violation.class));
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("Ошибка ResourceNotFoundException, если ID студента не существует")
    void assignViolations_NotFound() {
        Long badId = 999L;
        ViolationBulkRequest req = new ViolationBulkRequest(badId, "2026-04-02", ViolationType.NOISE);

        when(studentRepository.findById(badId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.assignViolationsToStudentsWithTx(List.of(req)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Студент не найден");

        verify(violationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Добавление нарушения студенту")
    void addViolationToStudent_Success() {
        Long studentId = 1L;
        Long violationId = 10L;

        Student student = Student.builder()
                .id(studentId)
                .violations(new HashSet<>())
                .build();

        Violation violation = Violation.builder()
                .id(violationId)
                .violationType(ViolationType.SMOKING)
                .students(new HashSet<>())  // Инициализируем students!
                .build();

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(violationService.findViolationById(violationId)).thenReturn(violation);
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        StudentResponseDto result = studentService.addViolationToStudent(studentId, violationId);

        assertThat(student.getViolations()).contains(violation);
        assertThat(violation.getStudents()).contains(student);
        verify(studentRepository).save(student);
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("Добавление нарушения - нарушение уже есть")
    void addViolationToStudent_AlreadyExists() {
        Long studentId = 1L;
        Long violationId = 10L;

        Violation violation = Violation.builder()
                .id(violationId)
                .students(new HashSet<>())
                .build();

        Student student = Student.builder()
                .id(studentId)
                .violations(new HashSet<>(Set.of(violation)))
                .build();

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(violationService.findViolationById(violationId)).thenReturn(violation);

        assertThatThrownBy(() -> studentService.addViolationToStudent(studentId, violationId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("уже есть");

        verify(studentRepository, never()).save(any());
    }
}