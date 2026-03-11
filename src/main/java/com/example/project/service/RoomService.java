package com.example.project.service;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.mapper.RoomMapper;
import com.example.project.model.Dormitory;
import com.example.project.model.Room;
import com.example.project.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final DormitoryService dormitoryService;
    public final RoomRepository roomRepository;
    public final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, DormitoryService dormitoryService) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.dormitoryService = dormitoryService;
    }

    public List<RoomResponseDto> findRoomsWithGraph() {
        List<Room> rooms = roomRepository.findAllWithGraph();
        return roomMapper.toDtoList(rooms);
    }

    public List<RoomResponseDto> findRooms() {
        List<Room> rooms = roomRepository.findAll();
        return roomMapper.toDtoList(rooms);
    }

    public Room findRoomEntityById(Long id) {
        return roomRepository.findRoomById(id);
    }

    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }

    public RoomResponseDto createRoom(Long dormitoryId, RoomRequestDto request) {
        Dormitory dormitory = dormitoryService.findDormitoryEntityById(dormitoryId);
        Room room = roomMapper.toEntity(request);

        room.setDormitory(dormitory);
        dormitory.getRooms().add(room);
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    public RoomResponseDto updateRoom(Long id, RoomRequestDto roomUpdates) {
        Room room = roomRepository.findRoomById(id);

        room.setNumber(roomUpdates.getNumber());
        room.setTotalPlaces(roomUpdates.getTotalPlaces());
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    public RoomResponseDto updatePatchRoom(Long id, RoomRequestDto roomUpdates) {
        Room room = roomRepository.findRoomById(id);

        if (roomUpdates.getNumber() != null) {
            room.setNumber(roomUpdates.getNumber());
        }

        if (roomUpdates.getTotalPlaces() != null) {
            room.setTotalPlaces(roomUpdates.getTotalPlaces());
        }

        roomRepository.save(room);
        return roomMapper.toDto(room);
    }
}