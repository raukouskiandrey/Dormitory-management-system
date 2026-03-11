package com.example.project.controller;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.model.Room;
import com.example.project.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) { this.roomService = roomService;}

    @GetMapping("")
    public ResponseEntity<List<RoomResponseDto>> getRooms(){
        return ResponseEntity.ok(roomService.findRooms());
    }

    @GetMapping("/withGraph")
    public ResponseEntity<List<RoomResponseDto>> getRoomsWithGraph(){
        return ResponseEntity.ok(roomService.findRoomsWithGraph());
    }

    @PostMapping("/{dormitoryId}")
    public ResponseEntity<RoomResponseDto> createRoom(@PathVariable Long dormitoryId,@RequestBody RoomRequestDto room){
        RoomResponseDto newRoom = roomService.createRoom(dormitoryId, room);
        return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoomById(@PathVariable Long id,@RequestBody RoomRequestDto room){
        return ResponseEntity.ok(roomService.updateRoom(id,room));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updatePatchRoomById(@PathVariable Long id,@RequestBody RoomRequestDto room){
        return ResponseEntity.ok(roomService.updatePatchRoom(id,room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long id){
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }
}
