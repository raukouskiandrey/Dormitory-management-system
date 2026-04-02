package com.example.project;

import com.example.project.dto.request.RoomRequestDto;
import com.example.project.exception.BadRequestException;
import com.example.project.model.Room;
import com.example.project.repository.RoomRepository;
import com.example.project.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void updatePatchRoom_ShouldFail_WhenTotalPlacesIsInvalid() {
        Room room = new Room();
        RoomRequestDto updateDto = new RoomRequestDto();
        updateDto.setTotalPlaces(10); // Невалидное значение (> 6)

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.updatePatchRoom(1L, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Количество мест в комнате должно быть от 1 до 6");
    }

    @Test
    void updatePatchRoom_ShouldFail_WhenRoomNumberIsNegative() {
        Room room = new Room();
        RoomRequestDto updateDto = new RoomRequestDto();
        updateDto.setNumber(-5);

        when(roomRepository.findRoomById(1L)).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> roomService.updatePatchRoom(1L, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Номер комнаты должен быть положительным числом");
    }
}