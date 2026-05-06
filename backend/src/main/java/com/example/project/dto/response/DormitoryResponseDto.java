package com.example.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Информация об общежитии")
public class DormitoryResponseDto {
    @Schema(description = "ID общежития", example = "1")
    Long id;

    @Schema(description = "Название", example = "Общежитие №4")
    private String name;

    @Schema(description = "Адрес", example = "ул. Университетская, д. 10")
    private String address;

    @Schema(description = "Список комнат в этом общежитии")
    private List<RoomResponseDto> rooms;
}
