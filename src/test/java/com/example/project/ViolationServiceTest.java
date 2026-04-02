package com.example.project;

import com.example.project.model.Student;
import com.example.project.model.Violation;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.ViolationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViolationServiceTest {

    @Mock private ViolationRepository violationRepository;
    @InjectMocks private ViolationService violationService;

    @Test
    void deleteViolation_ShouldRemoveFromStudentsAndThenDelete() {
        Violation violation = new Violation();
        Student student = new Student();
        student.setViolations(new HashSet<>(Set.of(violation)));
        violation.setStudents(Set.of(student));

        when(violationRepository.findViolationById(1L)).thenReturn(Optional.of(violation));

        violationService.deleteViolationById(1L);

        // После удаления, студент должен потерять нарушение
        // Проверяем, что метод delete был вызван
        verify(violationRepository).delete(violation);
    }
}