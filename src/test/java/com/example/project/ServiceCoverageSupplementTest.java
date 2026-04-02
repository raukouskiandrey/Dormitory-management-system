package com.example.project;

import com.example.project.dto.request.ViolationBulkRequest;
import com.example.project.model.Student;
import com.example.project.model.ViolationType;
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

    @Mock private StudentRepository studentRepository;
    @Mock private ViolationRepository violationRepository;
    @Mock private StudentMapper studentMapper;
    @InjectMocks private StudentService studentService;

    @Test
    @DisplayName("Массовое назначение нарушений — успех")
    void assignViolations_Success() {
        ViolationBulkRequest req = new ViolationBulkRequest(1L, "2024-01-01", ViolationType.SMOKING);

        Student student = Student.builder()
                .id(1L)
                .violations(new HashSet<>())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.assignViolationsToStudentsWithTx(List.of(req));

        verify(violationRepository, atLeastOnce()).save(any());
        verify(studentRepository, atLeastOnce()).save(any(Student.class));
    }
}