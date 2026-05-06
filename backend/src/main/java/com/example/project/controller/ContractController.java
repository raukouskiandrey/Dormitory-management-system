package com.example.project.controller;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contract")
@Tag(name = "Контракты", description = "Управление договорами найма жилого помещения")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("")
    @Operation(summary = "Получить все контракты", description = "Возвращает полный список существующих договоров")
    public ResponseEntity<List<ContractResponseDto>> getContracts() {
        return ResponseEntity.ok(contractService.findContacts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти контракт по ID", description = "Возвращает данные конкретного договора")
    public ResponseEntity<ContractResponseDto> getContract(
            @Parameter(description = "Идентификатор контракта", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(contractService.findContactById(id));
    }

    @PostMapping("/{studentId}")
    @Operation(summary = "Создать новый контракт", description = "Оформляет договор для студента по его идентификатору")
    public ResponseEntity<ContractResponseDto> createContract(
            @Parameter(description = "Идентификатор студента", example = "5")
            @PathVariable Long studentId,
            @Valid @RequestBody ContractRequestDto contract) {
        ContractResponseDto newContract = contractService.createContract(studentId, contract);
        return new ResponseEntity<>(newContract, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полное обновление контракта", description = "Заменяет все данные контракта по его ID")
    public ResponseEntity<ContractResponseDto> updateContractById(
            @Parameter(description = "Идентификатор обновляемого контракта", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ContractRequestDto contract) {
        return ResponseEntity.ok(contractService.updateContract(id, contract));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Частичное обновление контракта",
            description = "Обновляет только те поля договора, которые переданы в запросе")
    public ResponseEntity<ContractResponseDto> updatePatchContractById(
            @Parameter(description = "Идентификатор обновляемого контракта", example = "1")
            @PathVariable Long id,
            @RequestBody ContractRequestDto contract) {
        return ResponseEntity.ok(contractService.updatePatchContract(id, contract));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить контракт", description = "Удаляет запись о договоре из системы по его ID")
    public ResponseEntity<Void> deleteContractById(
            @Parameter(description = "Идентификатор удаляемого контракта", example = "1")
            @PathVariable Long id) {
        contractService.deleteContractById(id);
        return ResponseEntity.noContent().build();
    }
}