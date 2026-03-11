package com.example.project.controller;

import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.ViolationResponseDto;
import com.example.project.model.Contract;
import com.example.project.model.Violation;
import com.example.project.service.ViolationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/violation")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService){
        this.violationService = violationService;
    }

    @GetMapping("")
    public ResponseEntity<List<ViolationResponseDto>> getViolations(){
        return ResponseEntity.ok(violationService.findViolations());
    }

    @PostMapping("/{studentId}")
    public ResponseEntity<ViolationResponseDto> createViolation(@PathVariable Long studentId,@RequestBody ViolationRequestDto violation){
        ViolationResponseDto newViolation = violationService.createViolation(studentId,violation);
        return new ResponseEntity<>(newViolation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViolationResponseDto> updateViolationById(@PathVariable Long id,@RequestBody ViolationRequestDto violation){
        return ResponseEntity.ok(violationService.updateViolation(id,violation));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ViolationResponseDto> updatePatchViolationById(@PathVariable Long id,@RequestBody ViolationRequestDto violation){
        return ResponseEntity.ok(violationService.updatePatchViolation(id,violation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViolationById(@PathVariable Long id){
        violationService.deleteViolationById(id);
        return ResponseEntity.noContent().build();
    }
}