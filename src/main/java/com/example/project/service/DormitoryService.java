package com.example.project.service;

import com.example.project.dto.request.DormitoryRequestDto;
import com.example.project.dto.response.DormitoryResponseDto;
import com.example.project.mapper.DormitoryMapper;
import com.example.project.mapper.RoomMapper;
import com.example.project.model.Contract;
import com.example.project.model.Dormitory;
import com.example.project.model.Student;
import com.example.project.repository.DormitoryRepository;
import com.example.project.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DormitoryService {

    public final DormitoryMapper dormitoryMapper;
    public final DormitoryRepository dormitoryRepository;

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
        return dormitoryRepository.findDormitoryById(id);
    }

    public DormitoryResponseDto createDormitory(DormitoryRequestDto request) {
        Dormitory dormitory = dormitoryMapper.toEntity(request);
        dormitoryRepository.save(dormitory);
        return dormitoryMapper.toDto(dormitory);
    }

    public DormitoryResponseDto updateDormitory(Long id, DormitoryRequestDto updatedDormitory) {
        Dormitory dormitory = dormitoryRepository.findDormitoryById(id);

        dormitory.setName(updatedDormitory.getName());
        dormitory.setAddress(updatedDormitory.getAddress());
        dormitoryRepository.save(dormitory);
        return dormitoryMapper.toDto(dormitory);
    }

    public DormitoryResponseDto updatePatchDormitory(Long id, DormitoryRequestDto updatedDormitory) {
        Dormitory dormitory = dormitoryRepository.findDormitoryById(id);

        if (updatedDormitory.getName() != null) {
            dormitory.setName(updatedDormitory.getName());
        }

        if (updatedDormitory.getAddress() != null) {
            dormitory.setAddress(updatedDormitory.getAddress());
        }

        dormitoryRepository.save(dormitory);
        return dormitoryMapper.toDto(dormitory);
    }

    public void deleteDormitoryById(Long id) {
        dormitoryRepository.deleteById(id);
    }
}
