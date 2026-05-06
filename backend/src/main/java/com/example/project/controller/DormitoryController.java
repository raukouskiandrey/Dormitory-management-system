package com.example.project.controller;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.service.DormitoryService;
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
@RequestMapping("/dormitory")
@Tag(name = "Общежития", description = "Управление общежитиями и их адресными данными")
public class DormitoryController {
    private final DormitoryService dormitoryService;

    public DormitoryController(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    @GetMapping("")
    @Operation(
            summary = "Получить список всех общежитий", description = "Возвращает краткую информацию обо всех корпусах")
    public ResponseEntity<List<DormitoryResponseDto>> getDormitories() {
        return ResponseEntity.ok(dormitoryService.findDormitories());
    }

    @GetMapping("/withGraph")
    @Operation(
            summary = "Получить общежития с помощью EntityGraph",
            description = "Возвращает список общежитий сразу со вложенными данными , избегая проблемы N+1"
    )
    public ResponseEntity<List<DormitoryResponseDto>> getDormitoriesWithGraph() {
        return ResponseEntity.ok(dormitoryService.findDormitoriesWithGraph());
    }

    @PostMapping("")
    @Operation(summary = "Добавить новое общежитие", description = "Создает новую запись о  общежития")
    public ResponseEntity<DormitoryResponseDto> createDormitory(
            @Valid @RequestBody DormitoryRequestDto dormitory) {
        DormitoryResponseDto newDormitory = dormitoryService.createDormitory(dormitory);
        return new ResponseEntity<>(newDormitory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полное обновление данных общежития", description = "Заменяет все поля общежития по его ID")
    public ResponseEntity<DormitoryResponseDto> updateDormitoryById(
            @Parameter(description = "ID общежития", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody DormitoryRequestDto dormitory) {
        return ResponseEntity.ok(dormitoryService.updateDormitory(id, dormitory));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частичное обновление данных общежития", description = "Обновляет только переданные поля ")
    public ResponseEntity<DormitoryResponseDto> updatePatchStudentById(
            @Parameter(description = "ID общежития", example = "1")
            @PathVariable Long id,
            @RequestBody DormitoryRequestDto dormitory) {
        return ResponseEntity.ok(dormitoryService.updatePatchDormitory(id, dormitory));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить общежитие", description = "Удаляет запись из системы")
    public ResponseEntity<Void> deleteStudentById(
            @Parameter(description = "ID общежития", example = "1")
            @PathVariable Long id) {
        dormitoryService.deleteDormitoryById(id);
        return ResponseEntity.noContent().build();
    }
}