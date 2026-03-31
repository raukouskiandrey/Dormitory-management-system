package com.example.project.controller;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.service.ViolationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Список всех нарушений", description = "Возвращает историю всех зафиксированных нарушений")
    public ResponseEntity<List<ViolationResponseDto>> getViolations() {
        return ResponseEntity.ok(violationService.findViolations());
    }

    @PostMapping("/{studentId}")
    @Operation(summary = "Зафиксировать нарушение", description = "Создает запись о нарушении и привязывает её к конкретному студенту")
    public ResponseEntity<ViolationResponseDto> createViolation(
            @Parameter(description = "ID студента-нарушителя", example = "1")
            @PathVariable Long studentId,
            @Valid @RequestBody ViolationRequestDto violation) {
        ViolationResponseDto newViolation = violationService.createViolation(studentId, violation);
        return new ResponseEntity<>(newViolation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полное обновление данных о нарушении", description = "Позволяет полностью изменить информацию о зафиксированном инциденте")
    public ResponseEntity<ViolationResponseDto> updateViolationById(
            @Parameter(description = "ID записи о нарушении", example = "10")
            @PathVariable Long id,
            @Valid @RequestBody ViolationRequestDto violation) {
        return ResponseEntity.ok(violationService.updateViolation(id, violation));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частичное исправление нарушения", description = "Позволяет изменить только отдельные поля ")
    public ResponseEntity<ViolationResponseDto> updatePatchViolationById(
            @Parameter(description = "ID записи о нарушении", example = "10")
            @PathVariable Long id,
            @RequestBody ViolationRequestDto violation) {
        return ResponseEntity.ok(violationService.updatePatchViolation(id, violation));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить запись о нарушении", description = "Удаление нарушения")
    public ResponseEntity<Void> deleteViolationById(
            @Parameter(description = "ID удаляемой записи", example = "10")
            @PathVariable Long id) {
        violationService.deleteViolationById(id);
        return ResponseEntity.noContent().build();
    }
}