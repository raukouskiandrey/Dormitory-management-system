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
    @DisplayName("createRoom — некорректное кол-во мест - при создании нет валидации в сервисе, тест проверяет что исключение НЕ выбрасывается")
    void createRoom_invalidPlaces_noValidationInCreate() {
        Long dormitoryId = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(101);
        request.setTotalPlaces(10); // Невалидное значение, но сервис не валидирует при create

        Dormitory dormitory = new Dormitory();
        dormitory.setRooms(new HashSet<>());

        Room room = new Room();

        when(dormitoryService.findDormitoryEntityById(dormitoryId)).thenReturn(dormitory);
        when(roomRepository.existsByNumberAndDormitoryId(101, dormitoryId)).thenReturn(false);
        when(roomMapper.toEntity(request)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        // Исключение НЕ должно выбрасываться, так как в createRoom нет валидации
        assertDoesNotThrow(() -> roomService.createRoom(dormitoryId, request));
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
    @DisplayName("updatePatchRoom - некорректное кол-во мест (>6) - проверка валидации")
    void updatePatchRoom_invalidPlaces() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setTotalPlaces(10);

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

    // ========== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ ДЛЯ ПОЛНОГО ПОКРЫТИЯ ==========

    @Test
    @DisplayName("updateRoom - номер не меняется (равен старому) - проверка дубликата НЕ выполняется")
    void updateRoom_numberNotChanged() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(101); // тот же номер
        request.setTotalPlaces(4);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        roomService.updateRoom(id, request);

        // existsByNumberAndDormitoryId НЕ должен вызываться, так как номер не меняется
        verify(roomRepository, never()).existsByNumberAndDormitoryId(anyInt(), anyLong());
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("updatePatchRoom - номер не меняется (null в запросе) - проверка дубликата НЕ выполняется")
    void updatePatchRoom_numberNull() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(null); // номер не меняется
        request.setTotalPlaces(5);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        roomService.updatePatchRoom(id, request);

        assertEquals(5, room.getTotalPlaces());
        assertEquals(101, room.getNumber()); // не изменился
        verify(roomRepository, never()).existsByNumberAndDormitoryId(anyInt(), anyLong());
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("updatePatchRoom - totalPlaces null (не обновляется)")
    void updatePatchRoom_totalPlacesNull() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(200);
        request.setTotalPlaces(null); // не обновляется

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
        assertEquals(3, room.getTotalPlaces()); // не изменилось
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("updatePatchRoom - конфликт при смене номера (номер уже занят)")
    void updatePatchRoom_duplicateNumberConflict() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(999);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(999, 1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> roomService.updatePatchRoom(id, request));
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("validateRoomData - number = null (пропускается валидация)")
    void validateRoomData_numberNull() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(null);
        request.setTotalPlaces(3); // валидное значение

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(3);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        // Не должно быть исключения
        assertDoesNotThrow(() -> roomService.updatePatchRoom(id, request));
    }

    @Test
    @DisplayName("validateRoomData - totalPlaces = null (пропускается валидация)")
    void validateRoomData_totalPlacesNull() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(200);
        request.setTotalPlaces(null);

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

        assertDoesNotThrow(() -> roomService.updatePatchRoom(id, request));
    }

    @Test
    @DisplayName("validateRoomData - totalPlaces = 1 (минимальное значение - проходит валидацию)")
    void validateRoomData_totalPlacesMin() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(200);
        request.setTotalPlaces(1); // минимальное допустимое

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

        assertDoesNotThrow(() -> roomService.updatePatchRoom(id, request));
        assertEquals(1, room.getTotalPlaces());
    }

    @Test
    @DisplayName("validateRoomData - totalPlaces = 6 (максимальное значение - проходит валидацию)")
    void validateRoomData_totalPlacesMax() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(200);
        request.setTotalPlaces(6); // максимальное допустимое

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

        assertDoesNotThrow(() -> roomService.updatePatchRoom(id, request));
        assertEquals(6, room.getTotalPlaces());
    }

    @Test
    @DisplayName("validateRoomData - totalPlaces < 1 (валидация выбрасывает исключение)")
    void validateRoomData_totalPlacesLessThanMin() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setTotalPlaces(0); // меньше 1

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
    @DisplayName("updatePatchRoom - полное обновление (и номер и места)")
    void updatePatchRoom_fullUpdate() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(300);
        request.setTotalPlaces(4);

        Dormitory dormitory = new Dormitory();
        dormitory.setId(1L);

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(2);
        room.setDormitory(dormitory);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(300, 1L)).thenReturn(false);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        roomService.updatePatchRoom(id, request);

        assertEquals(300, room.getNumber());
        assertEquals(4, room.getTotalPlaces());
        verify(roomRepository).existsByNumberAndDormitoryId(300, 1L);
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("updatePatchRoom - номер в запросе совпадает с текущим (ветка false)")
    void updatePatchRoom_sameNumber() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setNumber(101); // Тот же номер, что и в базе

        Room room = new Room();
        room.setNumber(101);
        room.setDormitory(new Dormitory());
        room.getDormitory().setId(1L);
        room.setStudents(new HashSet<>());

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toDto(room)).thenReturn(new RoomResponseDto());

        roomService.updatePatchRoom(id, request);

        // Проверяем, что поиск дубликата в БД НЕ вызывался
        verify(roomRepository, never()).existsByNumberAndDormitoryId(anyInt(), anyLong());
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("updatePatchRoom - ошибка: лимит мест меньше количества проживающих")
    void updatePatchRoom_limitBelowCurrentStudents() {
        Long id = 1L;
        RoomRequestDto request = new RoomRequestDto();
        request.setTotalPlaces(1); // Пытаемся поставить 1 место

        Room room = new Room();
        room.setNumber(101);
        room.setStudents(Set.of(new Student(), new Student())); // В комнате уже 2 студента

        when(roomRepository.findRoomById(id)).thenReturn(Optional.of(room));

        // Проверяем текст ошибки, чтобы убедиться, что зашли именно в эту ветку
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> roomService.updatePatchRoom(id, request));

        assertTrue(ex.getMessage().contains("меньше текущего кол-ва студентов"));
    }
    
}