package com.example.project.service;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.DormitoryMapper;
import com.example.project.model.Dormitory;
import com.example.project.repository.DormitoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryService {

    public final DormitoryMapper dormitoryMapper;
    public final DormitoryRepository dormitoryRepository;
    private static final String DORMITORY_NOT_FOUND = "Dormitory not found with id: ";

    public DormitoryService(DormitoryRepository dormitoryRepository, DormitoryMapper dormitoryMapper) {
        this.dormitoryRepository = dormitoryRepository;
        this.dormitoryMapper = dormitoryMapper;
    }

    public List<DormitoryResponseDto> findDormitoriesWithGraph() {
        List<Dormitory> dormitories = dormitoryRepository.findAllWithGraph();
        return dormitoryMapper.toDtoList(dormitories);
    }

    public List<DormitoryResponseDto> findDormitories() {
        List<Dormitory> dormitories = dormitoryRepository.findAll();
        return dormitoryMapper.toDtoList(dormitories);
    }

    public Dormitory findDormitoryEntityById(Long id) {
        return dormitoryRepository.findDormitoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DORMITORY_NOT_FOUND + id));
    }

    public DormitoryResponseDto createDormitory(DormitoryRequestDto request) {
        Dormitory dormitory = dormitoryMapper.toEntity(request);
        if (dormitoryRepository.existsByNameAndAddress(request.getName(), request.getAddress())) {
            throw new BadRequestException("Общежитие с таким названием по этому адресу уже существует");
        }
        dormitoryRepository.save(dormitory);
        return dormitoryMapper.toDto(dormitory);
    }

    public DormitoryResponseDto updateDormitory(Long id, DormitoryRequestDto updatedDormitory) {
        Dormitory dormitory = dormitoryRepository.findDormitoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DORMITORY_NOT_FOUND + id));

        if ((!dormitory.getName().equals(updatedDormitory.getName())
                || !dormitory.getAddress().equals(updatedDormitory.getAddress()))
                && dormitoryRepository.existsByNameAndAddress(
                        updatedDormitory.getName(), updatedDormitory.getAddress())) {

            throw new BadRequestException("Общежитие с таким названием по этому адресу уже существует");
        }


        dormitory.setName(updatedDormitory.getName());
        dormitory.setAddress(updatedDormitory.getAddress());
        dormitoryRepository.save(dormitory);
        return dormitoryMapper.toDto(dormitory);
    }

    public DormitoryResponseDto updatePatchDormitory(Long id, DormitoryRequestDto updatedDormitory) {
        Dormitory dormitory = dormitoryRepository.findDormitoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DORMITORY_NOT_FOUND + id));

        String oldName = dormitory.getName();
        String oldAddress = dormitory.getAddress();

        if (updatedDormitory.getName() != null) {
            dormitory.setName(updatedDormitory.getName());
        }
        if (updatedDormitory.getAddress() != null) {
            dormitory.setAddress(updatedDormitory.getAddress());
        }

        if ((!oldName.equals(dormitory.getName()) || !oldAddress.equals(dormitory.getAddress()))
                && dormitoryRepository.existsByNameAndAddress(dormitory.getName(), dormitory.getAddress())) {

            throw new BadRequestException("Общежитие с таким названием по этому адресу уже существует");
        }


        dormitoryRepository.save(dormitory);
        return dormitoryMapper.toDto(dormitory);
    }

    public void deleteDormitoryById(Long id) {
        Dormitory dormitory = dormitoryRepository.findDormitoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DORMITORY_NOT_FOUND + id));
        dormitoryRepository.delete(dormitory);
    }
}
