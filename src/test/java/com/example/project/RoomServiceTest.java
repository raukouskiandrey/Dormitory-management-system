package com.example.project;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.RoomMapper;
import com.example.project.model.Dormitory;
import com.example.project.model.Room;
import com.example.project.model.Student;
import com.example.project.repository.RoomRepository;
import com.example.project.service.DormitoryService;
import com.example.project.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomMapper roomMapper;
    @Mock private DormitoryService dormitoryService;
    @InjectMocks private RoomService roomService;

    @Test
    @DisplayName("findRooms - успешное получение списка")
    void findRooms_success() {
        List<Room> rooms = List.of(new Room(), new Room());
        List<RoomResponseDto> expectedDtos = List.of(new RoomResponseDto(), new RoomResponseDto());

        when(roomRepository.findAll()).thenReturn(rooms);
        when(roomMapper.toDtoList(rooms)).thenReturn(expectedDtos);

        List<RoomResponseDto> result = roomService.findRooms();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findRoomsWithGraph - успешное получение с графом")
    void findRoomsWithGraph_success() {
        List<Room> rooms = List.of(new Room());
        List<RoomResponseDto> expectedDtos = List.of(new RoomResponseDto());

        when(roomRepository.findAllWithGraph()).thenReturn(rooms);
        when(roomMapper.toDtoList(rooms)).thenReturn(expectedDtos);

        List<RoomResponseDto> result = roomService.findRoomsWithGraph();

        assertNotNull(result);
        verify(roomRepository).findAllWithGraph();
    }

    @Test
    @DisplayName("findRoomEntityById - успешный поиск")
    void findRoomEntityById_success() {
        Long id = 1L;
        Room room = new Room();

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));

        Room result = roomService.findRoomEntityById(id);

        assertNotNull(result);
        assertEquals(room, result);
    }

    @Test
    @DisplayName("findRoomEntityById - не найден")
    void findRoomEntityById_notFound() {
        Long id = 999L;

        when(roomRepository.findRoomById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.findRoomEntityById(id));
    }

    @Test
    @DisplayName("createRoom - успешное создание")
    void createRoom_success() {
        Long dormitoryId = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(101);
        request.setTotalPlaces(3);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(dormitoryId);
        dormitory.setRooms(new HashSet<>());

        Room room = new Room();
        RoomResponseDto expectedDto = new RoomResponseDto();

        when(dormitoryService.findDormitoryEntityById(dormitoryId)).thenReturn(dormitory);
        when(roomRepository.existsByNumberAndDormitoryId(101, dormitoryId)).thenReturn(false);
        when(roomMapper.toEntity(request)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(expectedDto);

        RoomResponseDto result = roomService.createRoom(dormitoryId, request);

        assertNotNull(result);
        verify(roomRepository).save(room);
        assertEquals(dormitory, room.getDormitory());
    }

    @Test
    @DisplayName("createRoom — ошибка (дубликат номера)")
    void createRoom_duplicateNumber() {
        Long dormitoryId = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(101);
        request.setTotalPlaces(3);

        Dormitory dormitory = new Dormitory();
        when(dormitoryService.findDormitoryEntityById(dormitoryId)).thenReturn(dormitory);
        when(roomRepository.existsByNumberAndDormitoryId(101, dormitoryId)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> roomService.createRoom(dormitoryId, request));
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("createRoom — некорректное кол-во мест (>6) - проверка через валидацию")
    void createRoom_invalidPlaces() {
        Long dormitoryId = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(101);
        request.setTotalPlaces(10);

        Dormitory dormitory = new Dormitory();
        dormitory.setRooms(new HashSet<>());

        when(dormitoryService.findDormitoryEntityById(dormitoryId)).thenReturn(dormitory);
        when(roomRepository.existsByNumberAndDormitoryId(101, dormitoryId)).thenReturn(false);
        when(roomMapper.toEntity(request)).thenReturn(new Room());

        // Ошибка должна быть выброшена из-за валидации в RoomService.validateRoomData
        assertThrows(BadRequestException.class, () -> roomService.createRoom(dormitoryId, request));
    }

    @Test
    @DisplayName("updateRoom - успешное обновление")
    void updateRoom_success() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(102);
        request.setTotalPlaces(4);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(102, 1L)).thenReturn(false);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        roomService.updateRoom(id, request);

        assertEquals(102, room.getNumber());
        assertEquals(4, room.getTotalPlaces());
    }

    @Test
    @DisplayName("updateRoom - ошибка лимита мест")
    void updateRoom_limitError() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setTotalPlaces(1);

        Student student = new Student();
        Set<Student> students = new HashSet<>();
        students.add(student);
        students.add(new Student()); // 2 студента

        Dormitory dormitory = new Dormitory();
        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(students);

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));

        assertThrows(BadRequestException.class, () -> roomService.updateRoom(id, request));
    }

    @Test
    @DisplayName("updateRoom - дубликат номера")
    void updateRoom_duplicateNumber() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(999);
        request.setTotalPlaces(3);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(999, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> roomService.updateRoom(id, request));
    }

    @Test
    @DisplayName("updateRoom - комната не найдена")
    void updateRoom_notFound() {
        Long id = 999L;
        RoomRequestDto request = new RoomRequestDto();

        when(roomRepository.findRoomById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.updateRoom(id, request));
    }

    @Test
    @DisplayName("updatePatchRoom - успешное частичное обновление")
    void updatePatchRoom_success() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(200);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(200, 1L)).thenReturn(false);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        roomService.updatePatchRoom(id, request);

        assertEquals(200, room.getNumber());
        assertEquals(3, room.getTotalPlaces());
    }

    @Test
    @DisplayName("updatePatchRoom — ошибка лимита (студентов больше чем мест)")
    void updatePatchRoom_limitError() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setTotalPlaces(0);

        Student student = new Student();
        Set<Student> students = new HashSet<>();
        students.add(student);

        Dormitory dormitory = new Dormitory();
        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(2);
        room.setDormitory(dormitory);
        room.setStudents(students);

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));

        assertThrows(BadRequestException.class, () -> roomService.updatePatchRoom(id, request));
    }

    @Test
    @DisplayName("updatePatchRoom - отрицательный номер комнаты")
    void updatePatchRoom_negativeNumber() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(-5);

        Dormitory dormitory = new Dormitory();
        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));

        assertThrows(BadRequestException.class, () -> roomService.updatePatchRoom(id, request));
    }

    @Test
    @DisplayName("deleteRoomById — успех")
    void deleteRoom_success() {
        Long id = 1L;
        Room room = new Room();

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));

        assertDoesNotThrow(() -> roomService.deleteRoomById(id));
        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    @DisplayName("deleteRoomById - не найдена")
    void deleteRoom_notFound() {
        Long id = 999L;

        when(roomRepository.findRoomById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.deleteRoomById(id));
    }
}