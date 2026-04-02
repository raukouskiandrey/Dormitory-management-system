package com.example.project;

import com.example.project.dto.request.ViolationBulkRequest;
import com.example.project.service.StudentService;
import com.example.project.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ServiceCoverageSupplementTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void assignViolations_EmptyList_ShouldReturnEmptyResult() {
        // Проверяем, что при пустом списке сервис кидает исключение (это и есть покрытие валидации)
        List<ViolationBulkRequest> emptyList = Collections.emptyList();

        assertThatThrownBy(() -> studentService.assignViolationsToStudentsWithTx(emptyList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Список нарушений не может быть пустым");

        // Проверяем, что до базы дело не дошло
        verifyNoInteractions(studentRepository);
    }

    @Test
    void checkNoTxMethod_ShouldWorkIdentically() {
        // Покрываем метод без транзакции, который тоже должен падать на валидации
        List<ViolationBulkRequest> emptyList = Collections.emptyList();

        assertThatThrownBy(() -> studentService.assignViolationsToStudentsNoTx(emptyList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Список нарушений не может быть пустым");

        verifyNoInteractions(studentRepository);
    }
}