package com.example.project.service;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.mapper.ContractMapper;
import com.example.project.mapper.StudentMapper;
import com.example.project.model.Contract;
import com.example.project.model.Student;
import com.example.project.repository.ContractRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {
    private final StudentService studentService;
    public ContractRepository contractRepository;
    public ContractMapper contractMapper;

    public ContractService(ContractRepository contractRepository, ContractMapper contractMapper, StudentService studentService, StudentMapper studentMapper) {
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
        this.studentService = studentService;
    }

    public List<ContractResponseDto> findContacts(){
        List<Contract> contacts = contractRepository.findAll();
        return contractMapper.toDtoList(contacts);
    }

    public ContractResponseDto createContract(Long studentId,ContractRequestDto request) {
        Student student = studentService.findStudentEntityById(studentId);
        Contract contract = contractMapper.toEntity(request);

        contract.setStudent(student);
        student.setContract(contract);
        contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }

    public ContractResponseDto updateContract(Long id, ContractRequestDto updatedContract) {
        Contract contract = contractRepository.findContractById(id);

        contract.setNumber(updatedContract.getNumber());
        contract.setStartDate(updatedContract.getStartDate());
        contract.setEndDate(updatedContract.getEndDate());
        contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }

    public ContractResponseDto updatePatchContract(Long id, ContractRequestDto updatedContract) {
        Contract contract = contractRepository.findContractById(id);

        if (updatedContract.getNumber() != null) {
            contract.setNumber(updatedContract.getNumber());
        }

        if (updatedContract.getStartDate() != null) {
            contract.setStartDate(updatedContract.getStartDate());
        }

        if (updatedContract.getEndDate() != null) {
            contract.setEndDate(updatedContract.getEndDate());
        }
        contractRepository.save(contract);
        return contractMapper.toDto(contract);
    }

    public void deleteContractById(Long id) {
        contractRepository.deleteById(id);
    }

    public ContractResponseDto findContactById(Long id) {
        Contract contract = contractRepository.findContractById(id);
        return contractMapper.toDto(contract);
    }
}
