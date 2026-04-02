package com.example.project;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.exception.BadRequestException;
import com.example.project.model.Violation;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.ViolationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViolationServiceTest {

    @Mock
    private ViolationRepository violationRepository;

    @InjectMocks
    private ViolationService violationService;

    @Test
    void updatePatchViolation_ShouldFail_WhenDateIsInFuture() {
        Violation violation = new Violation();
        ViolationRequestDto updateDto = new ViolationRequestDto();
        updateDto.setDate("2099-01-01"); // Будущее

        when(violationRepository.findViolationById(1L)).thenReturn(Optional.of(violation));

        assertThatThrownBy(() -> violationService.updatePatchViolation(1L, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата нарушения не может быть в будущем");
    }
}