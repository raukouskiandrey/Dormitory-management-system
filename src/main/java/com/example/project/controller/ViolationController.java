package com.example.project.controller;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.model.enums.ViolationType;
import com.example.project.service.ViolationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/violation")
@Tag(name = "Нарушения", description = "Регистрация и учет дисциплинарных нарушений студентов")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping("")
    @Operation(summary = "Список всех нарушений")
    public ResponseEntity<List<ViolationResponseDto>> getViolations() {
        return ResponseEntity.ok(violationService.findViolations());
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Поиск нарушений с фильтрами",
            description = "Фильтр по типу нарушения, поиск по ФИО студента, сортировка по дате"
    )
    public ResponseEntity<List<ViolationResponseDto>> searchViolations(
            @RequestParam(required = false) ViolationType violationType,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false, defaultValue = "desc") String sortByDate
    ) {
        return ResponseEntity.ok(
                violationService.findViolationsFiltered(violationType, fio, sortByDate)
        );
    }

    @PostMapping("/{studentId}")
    @Operation(summary = "Зафиксировать нарушение")
    public ResponseEntity<ViolationResponseDto> createViolation(
            @PathVariable Long studentId,
            @Valid @RequestBody ViolationRequestDto violation) {
        ViolationResponseDto newViolation = violationService.createViolation(studentId, violation);
        return new ResponseEntity<>(newViolation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полное обновление данных о нарушении")
    public ResponseEntity<ViolationResponseDto> updateViolationById(
            @PathVariable Long id,
            @Valid @RequestBody ViolationRequestDto violation) {
        return ResponseEntity.ok(violationService.updateViolation(id, violation));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частичное исправление нарушения")
    public ResponseEntity<ViolationResponseDto> updatePatchViolationById(
            @PathVariable Long id,
            @RequestBody ViolationRequestDto violation) {
        return ResponseEntity.ok(violationService.updatePatchViolation(id, violation));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить запись о нарушении")
    public ResponseEntity<Void> deleteViolationById(@PathVariable Long id) {
        violationService.deleteViolationById(id);
        return ResponseEntity.noContent().build();
    }
}