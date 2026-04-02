package com.example.project;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.ContractMapper;
import com.example.project.model.Contract;
import com.example.project.model.Student;
import com.example.project.repository.ContractRepository;
import com.example.project.service.ContractService;
import com.example.project.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock private ContractRepository contractRepository;
    @Mock private ContractMapper contractMapper;
    @Mock private StudentService studentService;
    @InjectMocks private ContractService contractService;

    @Test
    @DisplayName("findContacts - успешное получение списка контрактов")
    void findContacts_success() {
        List<Contract> contracts = List.of(new Contract(), new Contract());
        List<ContractResponseDto> expectedDtos = List.of(new ContractResponseDto(), new ContractResponseDto());

        when(contractRepository.findAll()).thenReturn(contracts);
        when(contractMapper.toDtoList(contracts)).thenReturn(expectedDtos);

        List<ContractResponseDto> result = contractService.findContacts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contractRepository).findAll();
        verify(contractMapper).toDtoList(contracts);
    }

    @Test
    @DisplayName("createContract - успешное создание контракта")
    void createContract_success() {
        Long studentId = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setNumber(12345);
        request.setStartDate("2024-01-01");
        request.setEndDate("2024-12-31");

        Student student = new Student();
        Contract contract = new Contract();
        ContractResponseDto expectedDto = new ContractResponseDto();

        when(studentService.findStudentEntityById(studentId)).thenReturn(student);
        when(contractMapper.toEntity(request)).thenReturn(contract);
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(expectedDto);

        ContractResponseDto result = contractService.createContract(studentId, request);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(contractRepository).save(contract);
        assertEquals(student, contract.getStudent());
    }

    @Test
    @DisplayName("validateDates — дата конца раньше начала")
    void validateDates_error() {
        ContractRequestDto request = new ContractRequestDto();
        request.setNumber(12345);
        request.setStartDate("2024-05-01");
        request.setEndDate("2024-04-01");

        Student student = new Student();
        when(studentService.findStudentEntityById(1L)).thenReturn(student);
        when(contractMapper.toEntity(request)).thenReturn(new Contract());

        assertThrows(BadRequestException.class, () -> contractService.createContract(1L, request));
    }

    @Test
    @DisplayName("updateContract - успешное обновление")
    void updateContract_success() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setNumber(54321);
        request.setStartDate("2024-02-01");
        request.setEndDate("2024-11-30");

        Contract contract = new Contract();
        ContractResponseDto expectedDto = new ContractResponseDto();

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(expectedDto);

        ContractResponseDto result = contractService.updateContract(id, request);

        assertNotNull(result);
        verify(contractRepository).save(contract);
        assertEquals(54321, contract.getNumber());
        assertEquals("2024-02-01", contract.getStartDate());
        assertEquals("2024-11-30", contract.getEndDate());
    }

    @Test
    @DisplayName("updateContract - контракт не найден")
    void updateContract_notFound() {
        Long id = 999L;
        ContractRequestDto request = new ContractRequestDto();

        when(contractRepository.findContractById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractService.updateContract(id, request));
    }

    @Test
    @DisplayName("updatePatchContract - успешное частичное обновление")
    void updatePatchContract_success() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setNumber(99999);

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(new ContractResponseDto());

        contractService.updatePatchContract(id, request);

        assertEquals(99999, contract.getNumber());
        assertEquals("2024-01-01", contract.getStartDate()); // не изменилась
        assertEquals("2024-12-31", contract.getEndDate());   // не изменилась
        verify(contractRepository).save(contract);
    }

    @Test
    @DisplayName("updatePatchContract - обновление только startDate")
    void updatePatchContract_onlyStartDate() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setStartDate("2024-06-01");

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(new ContractResponseDto());

        contractService.updatePatchContract(id, request);

        assertEquals("2024-06-01", contract.getStartDate());
        assertEquals(11111, contract.getNumber());
        assertEquals("2024-12-31", contract.getEndDate());
    }

    @Test
    @DisplayName("updatePatchContract - контракт не найден")
    void updatePatchContract_notFound() {
        Long id = 999L;
        ContractRequestDto request = new ContractRequestDto();

        when(contractRepository.findContractById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractService.updatePatchContract(id, request));
    }

    @Test
    @DisplayName("deleteContractById - успешное удаление")
    void deleteContractById_success() {
        Long id = 1L;
        Contract contract = new Contract();

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));

        contractService.deleteContractById(id);

        verify(contractRepository).delete(contract);
    }

    @Test
    @DisplayName("deleteContractById - контракт не найден")
    void deleteContractById_notFound() {
        Long id = 999L;

        when(contractRepository.findContractById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractService.deleteContractById(id));
    }

    @Test
    @DisplayName("findContactById - успешный поиск")
    void findContactById_success() {
        Long id = 1L;
        Contract contract = new Contract();
        ContractResponseDto expectedDto = new ContractResponseDto();

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractMapper.toDto(contract)).thenReturn(expectedDto);

        ContractResponseDto result = contractService.findContactById(id);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("findContactById - контракт не найден")
    void findContactById_notFound() {
        Long id = 999L;

        when(contractRepository.findContractById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractService.findContactById(id));
    }

    @Test
    @DisplayName("updatePatchContract - все поля null (пустой PATCH запрос)")
    void updatePatchContract_allFieldsNull() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        // Все поля остаются null

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(new ContractResponseDto());

        // Должно выполниться успешно без изменений
        assertDoesNotThrow(() -> contractService.updatePatchContract(id, request));

        // Проверяем, что значения не изменились
        assertEquals(11111, contract.getNumber());
        assertEquals("2024-01-01", contract.getStartDate());
        assertEquals("2024-12-31", contract.getEndDate());

        verify(contractRepository).save(contract);
    }

    @Test
    @DisplayName("updatePatchContract - обновление только номера, даты null")
    void updatePatchContract_onlyNumberUpdate() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setNumber(99999);
        // startDate и endDate остаются null

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(new ContractResponseDto());

        contractService.updatePatchContract(id, request);

        assertEquals(99999, contract.getNumber());
        assertEquals("2024-01-01", contract.getStartDate()); // не изменилась
        assertEquals("2024-12-31", contract.getEndDate());   // не изменилась
        verify(contractRepository).save(contract);
    }

    @Test
    @DisplayName("updatePatchContract - обновление только startDate")
    void updatePatchContract_onlyStartDateUpdate() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setStartDate("2024-06-01");
        // number и endDate остаются null

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(new ContractResponseDto());

        contractService.updatePatchContract(id, request);

        assertEquals("2024-06-01", contract.getStartDate());
        assertEquals(11111, contract.getNumber());     // не изменился
        assertEquals("2024-12-31", contract.getEndDate()); // не изменилась
        verify(contractRepository).save(contract);
    }

    @Test
    @DisplayName("updatePatchContract - обновление только endDate")
    void updatePatchContract_onlyEndDateUpdate() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setEndDate("2024-11-30");
        // number и startDate остаются null

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toDto(contract)).thenReturn(new ContractResponseDto());

        contractService.updatePatchContract(id, request);

        assertEquals("2024-11-30", contract.getEndDate());
        assertEquals(11111, contract.getNumber());         // не изменился
        assertEquals("2024-01-01", contract.getStartDate()); // не изменилась
        verify(contractRepository).save(contract);
    }

    @Test
    @DisplayName("updatePatchContract - валидация дат после PATCH обновления")
    void updatePatchContract_validationAfterPatch() {
        Long id = 1L;
        ContractRequestDto request = new ContractRequestDto();
        request.setStartDate("2024-12-31");
        request.setEndDate("2024-01-01"); // endDate раньше startDate

        Contract contract = new Contract();
        contract.setNumber(11111);
        contract.setStartDate("2024-01-01");
        contract.setEndDate("2024-12-31");

        when(contractRepository.findContractById(id)).thenReturn(Optional.of(contract));

        // Должна выброситься ошибка валидации
        assertThrows(BadRequestException.class, () -> contractService.updatePatchContract(id, request));

        // save не должен вызываться
        verify(contractRepository, never()).save(any());
    }
}