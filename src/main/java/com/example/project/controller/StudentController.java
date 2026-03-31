package com.example.project.controller;

import com.example.project.dto.request.StudentCreationDto;
import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.ViolationType;
import com.example.project.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/student")
@Tag(name = "Студенты", description = "Управление данными студентов, их заселением, нарушениями и сложной фильтрацией")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    @Operation(
            summary = "Список всех студентов (пагинация)",
            description = "Возвращает страницу со списком всех студентов")
    public ResponseEntity<Page<StudentResponseDto>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ok(studentService.findStudentsPaged(page, size));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Найти студента по ID",
            description = "Возвращает информацию о студенте")
    public ResponseEntity<StudentResponseDto> getStudentsById(
            @Parameter(description = "ID студента", example = "1") @PathVariable Long id) {
        return ok(studentService.findStudentsById(id));
    }

    @GetMapping("")
    @Operation(
            summary = "Поиск студентов по возрасту",
            description = "Возвращает список студентов определенного возраста")
    public ResponseEntity<Page<StudentResponseDto>> getStudentsByAge(
            @RequestParam int age,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ok(studentService.findStudentsByAgePaged(age, page, size));
    }

    @PostMapping("/{studentId}/assign-to-room/{roomId}")
    @Operation(
            summary = "Заселить студента в комнату",
            description = "Устанавливает связь между студентом и комнатой")
    public ResponseEntity<StudentResponseDto> assignStudentToRoom(
            @Parameter(description = "ID студента") @PathVariable Long studentId,
            @Parameter(description = "ID целевой комнаты") @PathVariable Long roomId) {
        StudentResponseDto updatedStudent = studentService.assignStudentToRoom(studentId, roomId);
        return ok(updatedStudent);
    }

    @PostMapping("/{studentId}/add-violation/{violationId}")
    @Operation(
            summary = "Привязать нарушение к студенту",
            description = "Добавляет существующее нарушение в список нарушений студента")
    public ResponseEntity<StudentResponseDto> addViolationToStudent(
            @PathVariable Long studentId,
            @PathVariable Long violationId) {
        StudentResponseDto updatedStudent = studentService.addViolationToStudent(studentId, violationId);
        return ok(updatedStudent);
    }

    @PostMapping("/{roomId}")
    @Operation(
            summary = "Простое создание студента",
            description = "Создает студента и сразу прикрепляет его к комнате")
    public ResponseEntity<StudentResponseDto> createStudent(
            @PathVariable Long roomId,
            @Valid @RequestBody StudentRequestDto student) {
        StudentResponseDto newStudent = studentService.createStudent(roomId, student);
        return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Полное обновление студента",
            description = "Обновляет все данные студента по ID")
    public ResponseEntity<StudentResponseDto> updateStudentById(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto student) {
        return ok(studentService.updateStudent(id, student));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Частичное обновление студента",
            description = "Обновляет только присланные поля студента")
    public ResponseEntity<StudentResponseDto> updatePatchStudentById(
            @PathVariable Long id,
            @RequestBody StudentRequestDto student) {
        return ok(studentService.updatePatchStudent(id, student));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить студента",
            description = "Удаляет студента и связанные с ним данные")
    public ResponseEntity<Void> deleteStudentById(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/noTx")
    @Operation(
            summary = "Создание без транзакции (тест)",
            description = "Метод для демонстрации проблем с целостностью данных без @Transactional")
    public ResponseEntity<StudentResponseDto> createStudentNoTx(
            @Valid @RequestBody StudentCreationDto student) {
        return new ResponseEntity<>(studentService.creationStudentNoTx(student), HttpStatus.CREATED);
    }

    @PostMapping("/withTx")
    @Operation(
            summary = "Комплексное создание (Transactional)",
            description = "Метод для демонстрации проблем с целостностью данных с @Transactional")
    public ResponseEntity<StudentResponseDto> createStudentWithTx(
            @Valid @RequestBody StudentCreationDto student) {
        return new ResponseEntity<>(studentService.creationStudentWithTx(student), HttpStatus.CREATED);
    }

    @GetMapping("/filter/jpql")
    @Operation(
            summary = "Фильтр JPQL",
            description = "Сложный поиск по часам ОПТ и типу нарушения через JPQL")
    public ResponseEntity<Page<StudentResponseDto>> filterStudentsPaged(
            @RequestParam(required = false) Integer chs,
            @RequestParam(required = false) ViolationType violationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(studentService.filterStudentsWithJpqlPaged(chs, violationType, page, size));
    }

    @GetMapping("/filter/native")
    @Operation(
            summary = "Фильтр Native SQL",
            description = "Сложный поиск по часам ОПТ и типу нарушения через native")
    public ResponseEntity<Page<StudentResponseDto>> filterNativeStudentsPaged(
            @RequestParam(required = false) Integer chs,
            @RequestParam(required = false) String violationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(studentService.filterStudentsWithNativePaged(chs, violationType, page, size));
    }
}