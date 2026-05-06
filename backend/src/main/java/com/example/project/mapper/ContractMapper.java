package com.example.project.mapper;

import com.example.project.dto.request.ContractRequestDto;
import com.example.project.dto.response.ContractResponseDto;
import com.example.project.model.Contract;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    public List<ContractResponseDto> toDtoList(List<Contract> contracts);

    public ContractResponseDto toDto(Contract contract);

    public Contract toEntity(ContractRequestDto request);
}
