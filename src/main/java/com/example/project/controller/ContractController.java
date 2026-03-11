package com.example.project.controller;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contract")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("")
    public ResponseEntity<List<ContractResponseDto>> getContracts() {
        return ResponseEntity.ok(contractService.findContacts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDto> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findContactById(id));
    }

    @PostMapping("/{studentId}")
    public ResponseEntity<ContractResponseDto> createContract(
            @PathVariable Long studentId,
            @RequestBody ContractRequestDto contract) {
        ContractResponseDto newContract = contractService.createContract(studentId, contract);
        return new ResponseEntity<>(newContract, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractResponseDto> updateContractById(
            @PathVariable Long id,
            @RequestBody ContractRequestDto contract) {
        return ResponseEntity.ok(contractService.updateContract(id, contract));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContractResponseDto> updatePatchContractById(
            @PathVariable Long id,
            @RequestBody ContractRequestDto contract) {
        return ResponseEntity.ok(contractService.updatePatchContract(id, contract));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContractById(@PathVariable Long id) {
        contractService.deleteContractById(id);
        return ResponseEntity.noContent().build();
    }
}