package com.example.project;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.ViolationMapper;
import com.example.project.model.Student;
import com.example.project.model.Violation;
import com.example.project.model.ViolationType;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.ViolationService;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViolationServiceTest {

    @Mock private ViolationRepository violationRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private ViolationMapper violationMapper;
    @InjectMocks private ViolationService violationService;

    @Test
    @DisplayName("findViolations - успешное получение списка")
    void findViolations_success() {
        List<Violation> violations = List.of(new Violation(), new Violation());
        List<ViolationResponseDto> expectedDtos = List.of(new ViolationResponseDto(), new ViolationResponseDto());

        when(violationRepository.findAll()).thenReturn(violations);
        when(violationMapper.toDtoList(violations)).thenReturn(expectedDtos);

        List<ViolationResponseDto> result = violationService.findViolations();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findViolationById - успешный поиск")
    void findViolationById_success() {
        Long id = 1L;
        Violation violation = new Violation();

        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));

        Violation result = violationService.findViolationById(id);

        assertNotNull(result);
        assertEquals(violation, result);
    }

    @Test
    @DisplayName("findViolationById - не найдено")
    void findViolationById_notFound() {
        Long id = 999L;

        when(violationRepository.findViolationById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> violationService.findViolationById(id));
    }

    @Test
    @DisplayName("createViolation - успешное создание")
    void createViolation_success() {
        Long studentId = 1L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setDate(LocalDate.now().toString());
        request.setViolationType(ViolationType.SMOKING);

        Student student = new Student();
        student.setViolations(new HashSet<>());

        Violation violation = new Violation();
        violation.setStudents(new HashSet<>());
        ViolationResponseDto expectedDto = new ViolationResponseDto();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(violationMapper.toEntity(request)).thenReturn(violation);
        when(violationRepository.save(violation)).thenReturn(violation);
        when(violationMapper.toDto(violation)).thenReturn(expectedDto);

        ViolationResponseDto result = violationService.createViolation(studentId, request);

        assertNotNull(result);
        verify(violationRepository).save(violation);
        assertTrue(student.getViolations().contains(violation));
        assertTrue(violation.getStudents().contains(student));
    }

    @Test
    @DisplayName("createViolation - студент не найден")
    void createViolation_studentNotFound() {
        Long studentId = 999L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setDate(LocalDate.now().toString());

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> violationService.createViolation(studentId, request));
    }

    @Test
    @DisplayName("validateViolationDate — ошибка (будущее время)")
    void validateDate_future() {
        Long studentId = 1L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setDate(LocalDate.now().plusDays(1).toString());
        request.setViolationType(ViolationType.SMOKING);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student()));

        assertThrows(BadRequestException.class, () -> violationService.createViolation(studentId, request));
    }

    @Test
    @DisplayName("updateViolation - успешное обновление")
    void updateViolation_success() {
        Long id = 1L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setDate("2024-01-01");
        request.setViolationType(ViolationType.DRINKING);

        Violation violation = new Violation();
        violation.setDate("2023-01-01");
        violation.setViolationType(ViolationType.SMOKING);

        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));
        when(violationRepository.save(violation)).thenReturn(violation);
        when(violationMapper.toDto(violation)).thenReturn(new ViolationResponseDto());

        violationService.updateViolation(id, request);

        assertEquals("2024-01-01", violation.getDate());
        assertEquals(ViolationType.DRINKING, violation.getViolationType());
    }

    @Test
    @DisplayName("updateViolation - не найдено")
    void updateViolation_notFound() {
        Long id = 999L;
        ViolationRequestDto request = new ViolationRequestDto();

        when(violationRepository.findViolationById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> violationService.updateViolation(id, request));
    }

    @Test
    @DisplayName("updatePatchViolation - успешное частичное обновление")
    void updatePatchViolation_success() {
        Long id = 1L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setViolationType(ViolationType.NOISE);

        Violation violation = new Violation();
        violation.setDate("2024-01-01");
        violation.setViolationType(ViolationType.SMOKING);

        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));
        when(violationRepository.save(violation)).thenReturn(violation);
        when(violationMapper.toDto(violation)).thenReturn(new ViolationResponseDto());

        violationService.updatePatchViolation(id, request);

        assertEquals(ViolationType.NOISE, violation.getViolationType());
        assertEquals("2024-01-01", violation.getDate());
    }

    @Test
    @DisplayName("updatePatchViolation - обновление только даты")
    void updatePatchViolation_onlyDate() {
        Long id = 1L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setDate("2024-12-31");

        Violation violation = new Violation();
        violation.setDate("2024-01-01");
        violation.setViolationType(ViolationType.SMOKING);

        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));
        when(violationRepository.save(violation)).thenReturn(violation);
        when(violationMapper.toDto(violation)).thenReturn(new ViolationResponseDto());

        violationService.updatePatchViolation(id, request);

        assertEquals("2024-12-31", violation.getDate());
        assertEquals(ViolationType.SMOKING, violation.getViolationType());
    }

    @Test
    @DisplayName("updatePatchViolation - будущая дата")
    void updatePatchViolation_futureDate() {
        Long id = 1L;
        ViolationRequestDto request = new ViolationRequestDto();
        request.setDate(LocalDate.now().plusDays(1).toString());

        Violation violation = new Violation();
        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));

        assertThrows(BadRequestException.class, () -> violationService.updatePatchViolation(id, request));
    }

    @Test
    @DisplayName("deleteViolationById - успешное удаление")
    void deleteViolationById_success() {
        Long id = 1L;
        Violation violation = new Violation();
        violation.setStudents(new HashSet<>());

        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));

        violationService.deleteViolationById(id);

        verify(violationRepository).delete(violation);
    }

    @Test
    @DisplayName("deleteViolationById - не найдено")
    void deleteViolationById_notFound() {
        Long id = 999L;

        when(violationRepository.findViolationById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> violationService.deleteViolationById(id));
    }

    @Test
    @DisplayName("deleteViolationById - с удалением связей со студентами")
    void deleteViolationById_withStudents() {
        Long id = 1L;
        Student student1 = new Student();
        student1.setViolations(new HashSet<>());
        Student student2 = new Student();
        student2.setViolations(new HashSet<>());

        Violation violation = new Violation();
        violation.setStudents(new HashSet<>(Set.of(student1, student2)));

        student1.getViolations().add(violation);
        student2.getViolations().add(violation);

        when(violationRepository.findViolationById(id)).thenReturn(Optional.of(violation));

        violationService.deleteViolationById(id);

        assertFalse(student1.getViolations().contains(violation));
        assertFalse(student2.getViolations().contains(violation));
        verify(violationRepository).delete(violation);
    }
}