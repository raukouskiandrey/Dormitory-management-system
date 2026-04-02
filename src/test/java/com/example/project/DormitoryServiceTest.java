package com.example.project;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.exception.BadRequestException;
import com.example.project.mapper.DormitoryMapper;
import com.example.project.model.Dormitory;
import com.example.project.repository.DormitoryRepository;
import com.example.project.service.DormitoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DormitoryServiceTest {

    @Mock
    private DormitoryRepository dormitoryRepository;

    @Mock
    private DormitoryMapper dormitoryMapper;

    @InjectMocks
    private DormitoryService dormitoryService;

    @Test
    void updatePatchDormitory_ShouldFail_WhenNameAndAddressAlreadyExist() {
        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        DormitoryRequestDto updateDto = new DormitoryRequestDto();
        updateDto.setName("New Name");
        updateDto.setAddress("New Address");

        when(dormitoryRepository.findDormitoryById(1L)).thenReturn(Optional.of(dormitory));
        // Мокируем, что такое общежитие уже есть в базе
        when(dormitoryRepository.existsByNameAndAddress("New Name", "New Address")).thenReturn(true);

        assertThatThrownBy(() -> dormitoryService.updatePatchDormitory(1L, updateDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Общежитие с таким названием по этому адресу уже существует");
    }
}