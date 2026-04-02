package com.example.project;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.Dormitory;
import com.example.project.repository.DormitoryRepository;
import com.example.project.mapper.DormitoryMapper;
import com.example.project.service.DormitoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DormitoryServiceTest {

    @Mock private DormitoryRepository dormitoryRepository;
    @Mock private DormitoryMapper dormitoryMapper;
    @InjectMocks private DormitoryService dormitoryService;

    @Test
    void updatePatchDormitory_ShouldOnlyUpdateNonNullFields() {
        Dormitory dorm = new Dormitory();
        dorm.setName("Old");
        dorm.setAddress("Old Addr");

        DormitoryRequestDto dto = new DormitoryRequestDto();
        dto.setName("New"); // Address is null

        when(dormitoryRepository.findDormitoryById(1L)).thenReturn(Optional.of(dorm));

        dormitoryService.updatePatchDormitory(1L, dto);

        assertThat(dorm.getName()).isEqualTo("New");
        assertThat(dorm.getAddress()).isEqualTo("Old Addr"); // Остался прежним
        verify(dormitoryRepository).save(dorm);
    }

    @Test
    void findDormitoriesWithGraph_ShouldCallRepo() {
        dormitoryService.findDormitoriesWithGraph();
        verify(dormitoryRepository).findAllWithGraph();
    }
}