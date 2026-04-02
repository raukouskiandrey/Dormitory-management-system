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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock private ContractRepository contractRepository;
    @Mock private ContractMapper contractMapper;
    @Mock private StudentService studentService;

    @InjectMocks private ContractService contractService;

    @Test
    void findContacts_ShouldReturnList() {
        List<Contract> contracts = List.of(new Contract(), new Contract());
        List<ContractResponseDto> expectedDtos = List.of(new ContractResponseDto(), new ContractResponseDto());

        when(contractRepository.findAll()).thenReturn(contracts);
        when(contractMapper.toDtoList(contracts)).thenReturn(expectedDtos);

        List<ContractResponseDto> result = contractService.findContacts();

        assertThat(result).hasSize(2);
        verify(contractMapper).toDtoList(contracts);
    }

    @Test
    void updateContract_ShouldUpdateAllFieldsAndValidate() {
        Contract contract = new Contract();
        contract.setNumber(123);  // Integer
        contract.setStartDate("2023-01-01");
        contract.setEndDate("2023-12-01");

        ContractRequestDto dto = new ContractRequestDto();
        dto.setNumber(456);  // Integer, не String!
        dto.setStartDate("2024-01-01");
        dto.setEndDate("2024-12-01");

        ContractResponseDto responseDto = new ContractResponseDto();
        responseDto.setNumber(456);  // Integer

        when(contractRepository.findContractById(1L)).thenReturn(Optional.of(contract));
        when(contractMapper.toDto(any(Contract.class))).thenReturn(responseDto);

        ContractResponseDto result = contractService.updateContract(1L, dto);

        assertThat(contract.getNumber()).isEqualTo(456);
        assertThat(result.getNumber()).isEqualTo(456);
        verify(contractRepository).save(contract);
    }

    @Test
    void deleteContract_ShouldInvokeDelete() {
        Contract contract = new Contract();
        when(contractRepository.findContractById(1L)).thenReturn(Optional.of(contract));

        contractService.deleteContractById(1L);

        verify(contractRepository).delete(contract);
    }

    @Test
    void updateContract_WhenContractNotFound_ShouldThrowException() {
        ContractRequestDto dto = new ContractRequestDto();
        when(contractRepository.findContractById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.updateContract(999L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Контракт с id не найден:999");
    }

    @Test
    void updateContract_WhenEndDateBeforeStartDate_ShouldThrowException() {
        Contract contract = new Contract();
        contract.setNumber(123);
        contract.setStartDate("2023-01-01");
        contract.setEndDate("2023-12-01");

        ContractRequestDto dto = new ContractRequestDto();
        dto.setNumber(456);
        dto.setStartDate("2024-12-01");
        dto.setEndDate("2024-01-01");

        when(contractRepository.findContractById(1L)).thenReturn(Optional.of(contract));

        assertThatThrownBy(() -> contractService.updateContract(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("не может быть раньше");
    }
}