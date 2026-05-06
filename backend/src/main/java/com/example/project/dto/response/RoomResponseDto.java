package com.example.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "Информация о комнате")
public class RoomResponseDto {
    @Schema(description = "ID записи комнаты", example = "10")
    Long id;

    @Schema(description = "Номер комнаты", example = "302")
    private Integer number;

    @Schema(description = "Всего мест в комнате", example = "4")
    private Integer totalPlaces;

    @Schema(description = "Список проживающих студентов")
    private List<StudentResponseDto> students;
}
