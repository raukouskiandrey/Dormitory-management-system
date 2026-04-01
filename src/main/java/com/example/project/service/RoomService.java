package com.example.project.service;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
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
    private static final String ROOM_NOT_FOUND = "Комната с id не найдена:";

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
        return roomRepository.findRoomById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND + id));
    }

    public void deleteRoomById(Long id) {
        Room room = roomRepository.findRoomById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND + id));
        roomRepository.delete(room);
    }

    public RoomResponseDto createRoom(Long dormitoryId, RoomRequestDto request) {
        Dormitory dormitory = dormitoryService.findDormitoryEntityById(dormitoryId);
        if (roomRepository.existsByNumberAndDormitoryId(request.getNumber(), dormitoryId)) {
            throw new BadRequestException("Комната номер " + request.getNumber() + " уже существует в этом общежитии");
        }
        Room room = roomMapper.toEntity(request);
        room.setDormitory(dormitory);
        dormitory.getRooms().add(room);
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    public RoomResponseDto updateRoom(Long id, RoomRequestDto roomUpdates) {
        Room room = roomRepository.findRoomById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND + id));

        if (!room.getNumber().equals(roomUpdates.getNumber())
                && roomRepository.existsByNumberAndDormitoryId(
                roomUpdates.getNumber(), room.getDormitory().getId())) {

            throw new BadRequestException("Номер комнаты " + roomUpdates.getNumber() + " уже занят в этом общежитии");
        }

        if (roomUpdates.getTotalPlaces() < room.getStudents().size()) {
            throw new BadRequestException("Нельзя установить лимит " + roomUpdates.getTotalPlaces()
                    + ", так как в комнате уже живет " + room.getStudents().size() + " чел.");
        }

        room.setNumber(roomUpdates.getNumber());
        room.setTotalPlaces(roomUpdates.getTotalPlaces());
        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    public RoomResponseDto updatePatchRoom(Long id, RoomRequestDto roomUpdates) {
        Room room = roomRepository.findRoomById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROOM_NOT_FOUND + id));

        validateRoomData(roomUpdates.getNumber(), roomUpdates.getTotalPlaces());

        if (roomUpdates.getNumber() != null) {
            if (!room.getNumber().equals(roomUpdates.getNumber())
                    && roomRepository.existsByNumberAndDormitoryId(roomUpdates.getNumber(),
                    room.getDormitory().getId())) {
                throw new BadRequestException("Номер комнаты " + roomUpdates.getNumber()
                        + " уже занят в этом общежитии");
            }
            room.setNumber(roomUpdates.getNumber());
        }

        if (roomUpdates.getTotalPlaces() != null) {
            if (roomUpdates.getTotalPlaces() < room.getStudents().size()) {
                throw new BadRequestException("Лимит мест (" + roomUpdates.getTotalPlaces()
                        + ") меньше текущего кол-ва студентов (" + room.getStudents().size() + ")");
            }
            room.setTotalPlaces(roomUpdates.getTotalPlaces());
        }

        roomRepository.save(room);
        return roomMapper.toDto(room);
    }

    private void validateRoomData(Integer number, Integer totalPlaces) {
        if (number != null && number <= 0) {
            throw new BadRequestException("Номер комнаты должен быть положительным числом");
        }

        if (totalPlaces != null && (totalPlaces < 1 || totalPlaces > 6)) {
            throw new BadRequestException("Количество мест в комнате должно быть от 1 до 6");
        }
    }
}