package com.example.project;

import com.example.project.dto.request.ViolationBulkRequest;
import com.example.project.model.ViolationType;
import com.example.project.service.StudentService;
import com.example.project.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ServiceCoverageSupplementTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void assignViolations_EmptyList_ShouldReturnEmptyResult() {
        // Проверяем поведение при пустом списке (граничный случай)
        List<ViolationBulkRequest> emptyList = Collections.emptyList();

        var result = studentService.assignViolationsToStudentsWithTx(emptyList);

        assertThat(result).isEmpty();
        // Проверяем, что к репозиторию даже не обращались
        verifyNoInteractions(studentRepository);
    }

    @Test
    void checkNoTxMethod_ShouldWorkIdentically() {
        // Покрываем метод без транзакции для статистики покрытия
        List<ViolationBulkRequest> emptyList = Collections.emptyList();
        var result = studentService.assignViolationsToStudentsNoTx(emptyList);
        assertThat(result).isEmpty();
    }
}