package com.example.project;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.ContractMapper;
import com.example.project.model.Contract;
import com.example.project.repository.ContractRepository;
import com.example.project.service.ContractService;
import com.example.project.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private ContractService contractService;

    @Test
    void findContactById_ShouldThrowException_WhenNotFound() {
        when(contractRepository.findContractById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.findContactById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Контракт с id не найден:1");
    }

    @Test
    void updateContract_ShouldFail_WhenDatesAreInvalid() {
        Contract existingContract = new Contract();
        existingContract.setStartDate("2023-01-01");
        existingContract.setEndDate("2023-12-31");

        ContractRequestDto updateDto = new ContractRequestDto();
        updateDto.setStartDate("2024-01-01");
        updateDto.setEndDate("2023-01-01"); // Конец раньше начала

        when(contractRepository.findContractById(1L)).thenReturn(Optional.of(existingContract));

        assertThatThrownBy(() -> contractService.updateContract(1L, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата окончания контракта не может быть раньше даты начала");
    }
}