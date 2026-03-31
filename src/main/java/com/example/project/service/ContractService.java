package com.example.project.service;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.ContractMapper;
import com.example.project.mapper.StudentMapper;
import com.example.project.model.Contract;
import com.example.project.model.Student;
import com.example.project.repository.ContractRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContractService {
    private final StudentService studentService;
    public final ContractRepository contractRepository;
    public final ContractMapper contractMapper;

    public ContractService(ContractRepository contractRepository,
                           ContractMapper contractMapper,
                           StudentService studentService) {
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
        this.studentService = studentService;
    }

    public List<ContractResponseDto> findContacts() {
        List<Contract> contacts = contractRepository.findAll();
        return contractMapper.toDtoList(contacts);
    }

    public ContractResponseDto createContract(Long studentId,ContractRequestDto request) {
        Student student = studentService.findStudentEntityById(studentId);
        Contract contract = contractMapper.toEntity(request);

        validateDates(request.getStartDate(),request.getEndDate());

        contract.setStudent(student);
        student.setContract(contract);
        contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }

    public ContractResponseDto updateContract(Long id, ContractRequestDto updatedContract) {
        Contract contract = contractRepository.findContractById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contracts not found with id: " + id));

        validateDates(updatedContract.getStartDate(),updatedContract.getEndDate());

        contract.setNumber(updatedContract.getNumber());
        contract.setStartDate(updatedContract.getStartDate());
        contract.setEndDate(updatedContract.getEndDate());
        contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }

    public ContractResponseDto updatePatchContract(Long id, ContractRequestDto updatedContract) {
        Contract contract = contractRepository.findContractById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contracts not found with id: " + id));

        if (updatedContract.getNumber() != null) {
            contract.setNumber(updatedContract.getNumber());
        }

        if (updatedContract.getStartDate() != null) {
            contract.setStartDate(updatedContract.getStartDate());
        }

        if (updatedContract.getEndDate() != null) {
            contract.setEndDate(updatedContract.getEndDate());
        }
        validateDates(contract.getStartDate(),contract.getEndDate());
        contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }

    public void deleteContractById(Long id) {
        Contract contract = contractRepository.findContractById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contracts not found with id: " + id));
        contractRepository.delete(contract);
    }

    public ContractResponseDto findContactById(Long id) {
        Contract contract = contractRepository.findContractById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contracts not found with id: " + id));
        return contractMapper.toDto(contract);
    }

    private void validateDates(String startStr, String endStr) {
        if (startStr == null || endStr == null) {
            return;
        }
        LocalDate start = LocalDate.parse(startStr);
        LocalDate end = LocalDate.parse(endStr);
        if (end.isBefore(start)) {
            throw new BadRequestException("Дата окончания контракта не может быть раньше даты начала");
        }
    }
}
