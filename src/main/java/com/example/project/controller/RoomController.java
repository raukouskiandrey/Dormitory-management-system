package com.example.project.controller;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@Tag(name = "Комнаты", description = "Управление комнатами общежитий")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("")
    @Operation(summary = "Получить все комнаты", description = "Возвращает список всех комнат всех общежитий")
    public ResponseEntity<List<RoomResponseDto>> getRooms() {
        return ResponseEntity.ok(roomService.findRooms());
    }

    @GetMapping("/withGraph")
    @Operation(
            summary = "Получить комнаты с детализацией с помощью EntityGraph",
            description = "Возвращает комнаты вместе с данными об общежитии и проживающих студентах за один запрос"
    )
    public ResponseEntity<List<RoomResponseDto>> getRoomsWithGraph() {
        return ResponseEntity.ok(roomService.findRoomsWithGraph());
    }

    @PostMapping("/{dormitoryId}")
    @Operation(summary = "Создать комнату", description = "Добавляет новую комнату в указанное общежитие")
    public ResponseEntity<RoomResponseDto> createRoom(
            @Parameter(description = "ID общежития, в котором создается комната", example = "1")
            @PathVariable Long dormitoryId,
            @Valid @RequestBody RoomRequestDto room) {
        RoomResponseDto newRoom = roomService.createRoom(dormitoryId, room);
        return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Полное обновление комнаты", description = "Обновляет все параметры комнаты по её ID")
    public ResponseEntity<RoomResponseDto> updateRoomById(
            @Parameter(description = "ID комнаты", example = "10")
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDto room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Частичное обновление комнаты", description = "Изменяет только переданные поля ")
    public ResponseEntity<RoomResponseDto> updatePatchRoomById(
            @Parameter(description = "ID комнаты", example = "10")
            @PathVariable Long id,
            @RequestBody RoomRequestDto room) {
        return ResponseEntity.ok(roomService.updatePatchRoom(id, room));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить комнату", description = "Удаляет комнату из системы по её ID")
    public ResponseEntity<Void> deleteRoomById(
            @Parameter(description = "ID удаляемой комнаты", example = "10")
            @PathVariable Long id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}