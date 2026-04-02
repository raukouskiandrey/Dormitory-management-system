package com.example.project;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.dto.response.RoomResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.RoomMapper;
import com.example.project.model.Dormitory;
import com.example.project.model.Room;
import com.example.project.repository.RoomRepository;
import com.example.project.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomMapper roomMapper;
    @InjectMocks private RoomService roomService;

    @Test
    void updatePatchRoom_ShouldFailIfTotalPlacesInvalid() {
        Room room = new Room();
        room.setStudents(new HashSet<>());

        RoomRequestDto dto = new RoomRequestDto();
        dto.setTotalPlaces(0);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.updatePatchRoom(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Количество мест в комнате должно быть от 1 до 6");
    }

    @Test
    void updatePatchRoom_ShouldFailIfTotalPlacesTooHigh() {
        Room room = new Room();
        room.setStudents(new HashSet<>());

        RoomRequestDto dto = new RoomRequestDto();
        dto.setTotalPlaces(7);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.updatePatchRoom(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Количество мест в комнате должно быть от 1 до 6");
    }

    @Test
    void updatePatchRoom_ShouldFailIfRoomNumberExistsInDormitory() {
        Dormitory dorm = new Dormitory();
        dorm.setId(10L);
        Room room = new Room();
        room.setNumber(101);
        room.setDormitory(dorm);
        room.setStudents(new HashSet<>());

        RoomRequestDto dto = new RoomRequestDto();
        dto.setNumber(102);
        dto.setTotalPlaces(4);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(102, 10L)).thenReturn(true);

        assertThatThrownBy(() -> roomService.updatePatchRoom(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("уже занят в этом общежитии");
    }

    @Test
    void updatePatchRoom_ShouldUpdateSuccessfully() {
        Dormitory dorm = new Dormitory();
        dorm.setId(10L);

        Room room = new Room();
        room.setId(1L);
        room.setNumber(101);
        room.setTotalPlaces(4);
        room.setDormitory(dorm);
        room.setStudents(new HashSet<>());

        RoomRequestDto dto = new RoomRequestDto();
        dto.setNumber(103);
        dto.setTotalPlaces(5);

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setId(1L);
        responseDto.setNumber(103);
        responseDto.setTotalPlaces(5);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(103, 10L)).thenReturn(false);
        when(roomMapper.toDto(any(Room.class))).thenReturn(responseDto);

        RoomResponseDto result = roomService.updatePatchRoom(1L, dto);

        assertThat(room.getNumber()).isEqualTo(103);
        assertThat(room.getTotalPlaces()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(103);
        assertThat(result.getTotalPlaces()).isEqualTo(5);
        verify(roomRepository).save(room);
        verify(roomMapper).toDto(room);
    }

    @Test
    void updatePatchRoom_ShouldUpdateOnlyNumber() {
        Dormitory dorm = new Dormitory();
        dorm.setId(10L);

        Room room = new Room();
        room.setId(1L);
        room.setNumber(101);
        room.setTotalPlaces(4);
        room.setDormitory(dorm);
        room.setStudents(new HashSet<>());

        RoomRequestDto dto = new RoomRequestDto();
        dto.setNumber(103);
        dto.setTotalPlaces(null);

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setId(1L);
        responseDto.setNumber(103);
        responseDto.setTotalPlaces(4);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNumberAndDormitoryId(103, 10L)).thenReturn(false);
        when(roomMapper.toDto(any(Room.class))).thenReturn(responseDto);

        RoomResponseDto result = roomService.updatePatchRoom(1L, dto);

        assertThat(room.getNumber()).isEqualTo(103);
        assertThat(room.getTotalPlaces()).isEqualTo(4);
        assertThat(result.getNumber()).isEqualTo(103);
        verify(roomRepository).save(room);
    }

    @Test
    void updatePatchRoom_ShouldUpdateOnlyTotalPlaces() {
        Dormitory dorm = new Dormitory();
        dorm.setId(10L);

        Room room = new Room();
        room.setId(1L);
        room.setNumber(101);
        room.setTotalPlaces(4);
        room.setDormitory(dorm);
        room.setStudents(new HashSet<>());

        RoomRequestDto dto = new RoomRequestDto();
        dto.setNumber(null);
        dto.setTotalPlaces(5);

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setId(1L);
        responseDto.setNumber(101);
        responseDto.setTotalPlaces(5);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(any(Room.class))).thenReturn(responseDto);

        RoomResponseDto result = roomService.updatePatchRoom(1L, dto);

        assertThat(room.getNumber()).isEqualTo(101);
        assertThat(room.getTotalPlaces()).isEqualTo(5);
        assertThat(result.getTotalPlaces()).isEqualTo(5);
        verify(roomRepository).save(room);
    }

    @Test
    void updatePatchRoom_ShouldFailWhenRoomNotFound() {
        RoomRequestDto dto = new RoomRequestDto();
        when(roomRepository.findRoomById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updatePatchRoom(999L, dto))
                .isInstanceOf(ResourceNotFoundException.class)  // Исправлено!
                .hasMessageContaining("Комната с id не найдена:999");
    }
}