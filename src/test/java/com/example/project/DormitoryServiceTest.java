package com.example.project;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.DormitoryMapper;
import com.example.project.model.Dormitory;
import com.example.project.repository.DormitoryRepository;
import com.example.project.service.DormitoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DormitoryServiceTest {

    @Mock private DormitoryRepository dormitoryRepository;
    @Mock private DormitoryMapper dormitoryMapper;
    @InjectMocks private DormitoryService dormitoryService;

    @Test
    @DisplayName("findDormitories - успешное получение списка")
    void findDormitories_success() {
        List<Dormitory> dormitories = List.of(new Dormitory(), new Dormitory());
        List<DormitoryResponseDto> expectedDtos = List.of(new DormitoryResponseDto(), new DormitoryResponseDto());

        when(dormitoryRepository.findAll()).thenReturn(dormitories);
        when(dormitoryMapper.toDtoList(dormitories)).thenReturn(expectedDtos);

        List<DormitoryResponseDto> result = dormitoryService.findDormitories();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findDormitoriesWithGraph - успешное получение с графом")
    void findDormitoriesWithGraph_success() {
        List<Dormitory> dormitories = List.of(new Dormitory());
        List<DormitoryResponseDto> expectedDtos = List.of(new DormitoryResponseDto());

        when(dormitoryRepository.findAllWithGraph()).thenReturn(dormitories);
        when(dormitoryMapper.toDtoList(dormitories)).thenReturn(expectedDtos);

        List<DormitoryResponseDto> result = dormitoryService.findDormitoriesWithGraph();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dormitoryRepository).findAllWithGraph();
    }

    @Test
    @DisplayName("createDormitory - успешное создание")
    void createDormitory_success() {
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("Dorm 1");
        request.setAddress("Street 1");

        Dormitory dormitory = new Dormitory();
        DormitoryResponseDto expectedDto = new DormitoryResponseDto();

        when(dormitoryRepository.existsByNameAndAddress("Dorm 1", "Street 1")).thenReturn(false);
        when(dormitoryMapper.toEntity(request)).thenReturn(dormitory);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(expectedDto);

        DormitoryResponseDto result = dormitoryService.createDormitory(request);

        assertNotNull(result);
        verify(dormitoryRepository).save(dormitory);
    }

    @Test
    @DisplayName("createDormitory — конфликт (уже существует)")
    void createDormitory_conflict() {
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("Dorm 1");
        request.setAddress("Street 1");

        when(dormitoryRepository.existsByNameAndAddress("Dorm 1", "Street 1")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> dormitoryService.createDormitory(request));
        verify(dormitoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("findDormitoryEntityById - успешный поиск")
    void findDormitoryEntityById_success() {
        Long id = 1L;
        Dormitory dormitory = new Dormitory();

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));

        Dormitory result = dormitoryService.findDormitoryEntityById(id);

        assertNotNull(result);
        assertEquals(dormitory, result);
    }

    @Test
    @DisplayName("findDormitoryEntityById - не найден")
    void findDormitoryEntityById_notFound() {
        Long id = 999L;

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dormitoryService.findDormitoryEntityById(id));
    }

    @Test
    @DisplayName("updateDormitory - успешное обновление")
    void updateDormitory_success() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("New Name");
        request.setAddress("New Address");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("New Name", "New Address")).thenReturn(false);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        dormitoryService.updateDormitory(id, request);

        assertEquals("New Name", dormitory.getName());
        assertEquals("New Address", dormitory.getAddress());
    }

    @Test
    @DisplayName("updateDormitory - конфликт при обновлении")
    void updateDormitory_conflict() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("Existing Name");
        request.setAddress("Existing Address");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("Existing Name", "Existing Address")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> dormitoryService.updateDormitory(id, request));
    }

    @Test
    @DisplayName("updateDormitory - не найден")
    void updateDormitory_notFound() {
        Long id = 999L;
        DormitoryRequestDto request = new DormitoryRequestDto();

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dormitoryService.updateDormitory(id, request));
    }

    @Test
    @DisplayName("updatePatchDormitory - успешное частичное обновление")
    void updatePatchDormitory_success() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("New Name Only");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("New Name Only", "Old Address")).thenReturn(false);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        dormitoryService.updatePatchDormitory(id, request);

        assertEquals("New Name Only", dormitory.getName());
        assertEquals("Old Address", dormitory.getAddress());
    }

    @Test
    @DisplayName("updatePatchDormitory - только address")
    void updatePatchDormitory_onlyAddress() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setAddress("New Address Only");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("Old Name", "New Address Only")).thenReturn(false);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        dormitoryService.updatePatchDormitory(id, request);

        assertEquals("Old Name", dormitory.getName());
        assertEquals("New Address Only", dormitory.getAddress());
    }

    @Test
    @DisplayName("deleteDormitoryById - успешное удаление")
    void deleteDormitoryById_success() {
        Long id = 1L;
        Dormitory dormitory = new Dormitory();

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));

        dormitoryService.deleteDormitoryById(id);

        verify(dormitoryRepository).delete(dormitory);
    }

    @Test
    @DisplayName("deleteDormitoryById - не найден")
    void deleteDormitoryById_notFound() {
        Long id = 999L;

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dormitoryService.deleteDormitoryById(id));
    }
    @Test
    @DisplayName("updateDormitory - обновление без конфликта (имя и адрес свободны)")
    void updateDormitory_noConflict_success() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("New Name");
        request.setAddress("New Address");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("New Name", "New Address")).thenReturn(false);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        DormitoryResponseDto result = dormitoryService.updateDormitory(id, request);

        assertNotNull(result);
        assertEquals("New Name", dormitory.getName());
        assertEquals("New Address", dormitory.getAddress());
        verify(dormitoryRepository).save(dormitory);
    }

    @Test
    @DisplayName("updateDormitory - имя и адрес не меняются (равны старым)")
    void updateDormitory_noChange() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("Old Name");
        request.setAddress("Old Address");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        // existsByNameAndAddress НЕ должен вызываться, так как значения не изменились
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        DormitoryResponseDto result = dormitoryService.updateDormitory(id, request);

        assertNotNull(result);
        verify(dormitoryRepository, never()).existsByNameAndAddress(anyString(), anyString());
        verify(dormitoryRepository).save(dormitory);
    }

    @Test
    @DisplayName("updatePatchDormitory - все поля null (пустой PATCH запрос)")
    void updatePatchDormitory_allFieldsNull() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        // Все поля null

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        DormitoryResponseDto result = dormitoryService.updatePatchDormitory(id, request);

        assertNotNull(result);
        // Проверяем, что значения не изменились
        assertEquals("Old Name", dormitory.getName());
        assertEquals("Old Address", dormitory.getAddress());
        // existsByNameAndAddress НЕ должен вызываться
        verify(dormitoryRepository, never()).existsByNameAndAddress(anyString(), anyString());
        verify(dormitoryRepository).save(dormitory);
    }

    @Test
    @DisplayName("updatePatchDormitory - обновление только имени (успешно)")
    void updatePatchDormitory_onlyNameUpdate_success() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("New Name Only");
        // address = null

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("New Name Only", "Old Address")).thenReturn(false);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        DormitoryResponseDto result = dormitoryService.updatePatchDormitory(id, request);

        assertNotNull(result);
        assertEquals("New Name Only", dormitory.getName());
        assertEquals("Old Address", dormitory.getAddress()); // не изменился
        verify(dormitoryRepository).save(dormitory);
    }

    @Test
    @DisplayName("updatePatchDormitory - обновление только адреса (успешно)")
    void updatePatchDormitory_onlyAddressUpdate_success() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setAddress("New Address Only");
        // name = null

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.existsByNameAndAddress("Old Name", "New Address Only")).thenReturn(false);
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        DormitoryResponseDto result = dormitoryService.updatePatchDormitory(id, request);

        assertNotNull(result);
        assertEquals("Old Name", dormitory.getName()); // не изменилось
        assertEquals("New Address Only", dormitory.getAddress());
        verify(dormitoryRepository).save(dormitory);
    }

    @Test
    @DisplayName("updatePatchDormitory - имя и адрес не меняются (равны старым)")
    void updatePatchDormitory_noChange() {
        Long id = 1L;
        DormitoryRequestDto request = new DormitoryRequestDto();
        request.setName("Old Name");
        request.setAddress("Old Address");

        Dormitory dormitory = new Dormitory();
        dormitory.setName("Old Name");
        dormitory.setAddress("Old Address");

        when(dormitoryRepository.findDormitoryById(id)).thenReturn(Optional.of(dormitory));
        when(dormitoryRepository.save(dormitory)).thenReturn(dormitory);
        when(dormitoryMapper.toDto(dormitory)).thenReturn(new DormitoryResponseDto());

        DormitoryResponseDto result = dormitoryService.updatePatchDormitory(id, request);

        assertNotNull(result);
        // Проверка уникальности НЕ должна выполняться, так как значения не изменились
        verify(dormitoryRepository, never()).existsByNameAndAddress(anyString(), anyString());
        verify(dormitoryRepository).save(dormitory);
    }
}