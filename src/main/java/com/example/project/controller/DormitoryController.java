package com.example.project.controller;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.service.DormitoryService;
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
@RequestMapping("/dormitory")
public class DormitoryController {
    private final DormitoryService dormitoryService;

    public DormitoryController(DormitoryService dormitoryService) {
        this.dormitoryService = dormitoryService;
    }

    @GetMapping("")
    public ResponseEntity<List<DormitoryResponseDto>> getDormitories() {
        return ResponseEntity.ok(dormitoryService.findDormitories());
    }

    @GetMapping("/withGraph")
    public ResponseEntity<List<DormitoryResponseDto>> getDormitoriesWithGraph() {
        return ResponseEntity.ok(dormitoryService.findDormitoriesWithGraph());
    }

    @PostMapping("")
    public ResponseEntity<DormitoryResponseDto> createDormitory(
            @RequestBody DormitoryRequestDto dormitory) {
        DormitoryResponseDto newDormitory = dormitoryService.createDormitory(dormitory);
        return new ResponseEntity<>(newDormitory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DormitoryResponseDto> updateDormitoryById(
            @PathVariable Long id,
            @RequestBody DormitoryRequestDto dormitory) {
        return ResponseEntity.ok(dormitoryService.updateDormitory(id, dormitory));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DormitoryResponseDto> updatePatchStudentById(
            @PathVariable Long id,
            @RequestBody DormitoryRequestDto dormitory) {
        return ResponseEntity.ok(dormitoryService.updatePatchDormitory(id, dormitory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable Long id) {
        dormitoryService.deleteDormitoryById(id);
        return ResponseEntity.noContent().build();
    }
}