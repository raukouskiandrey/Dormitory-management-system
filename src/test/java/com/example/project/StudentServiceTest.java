package com.example.project;

import com.example.project.cache.CacheManager;
import com.example.project.dto.request.*;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.mapper.StudentMapper;
import com.example.project.model.*;
import com.example.project.repository.ContractRepository;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import com.example.project.service.RoomService;
import com.example.project.service.StudentService;
import com.example.project.service.ViolationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private StudentMapper studentMapper;
    @Mock private RoomService roomService;
    @Mock private ContractRepository contractRepository;
    @Mock private CacheManager cacheManager;
    @Mock private ViolationService violationService;
    @Mock private ViolationRepository violationRepository;
    @InjectMocks private StudentService studentService;

    @Test
    @DisplayName("findStudentsPaged - успешное получение страницы")
    void findStudentsPaged_success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<Student> students = List.of(new Student(), new Student());
        Page<Student> studentPage = new PageImpl<>(students, pageable, 2);
        List<StudentResponseDto> expectedDtos = List.of(new StudentResponseDto(), new StudentResponseDto());

        when(studentRepository.findAll(any(Pageable.class))).thenReturn(studentPage);
        when(studentMapper.toDto(students.get(0))).thenReturn(expectedDtos.get(0));
        when(studentMapper.toDto(students.get(1))).thenReturn(expectedDtos.get(1));

        Page<StudentResponseDto> result = studentService.findStudentsPaged(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(studentRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("findStudentsByAgePaged - успешно")
    void findStudentsByAgePaged_success() {
        int age = 20;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        List<Student> students = List.of(new Student());
        Page<Student> studentPage = new PageImpl<>(students, pageable, 1);

        when(studentRepository.findByAge(eq(age), any(Pageable.class))).thenReturn(studentPage);
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        Page<StudentResponseDto> result = studentService.findStudentsByAgePaged(age, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("findStudentsByAgePaged - невалидный возраст")
    void findStudentsByAgePaged_invalidAge() {
        assertThrows(BadRequestException.class, () -> studentService.findStudentsByAgePaged(10, 0, 10));
        assertThrows(BadRequestException.class, () -> studentService.findStudentsByAgePaged(150, 0, 10));
    }

    @Test
    @DisplayName("findStudentsById - успешно")
    void findStudentsById_success() {
        Long id = 1L;
        Student student = new Student();
        StudentResponseDto expectedDto = new StudentResponseDto();

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(expectedDto);

        StudentResponseDto result = studentService.findStudentsById(id);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("findStudentsById - не найден")
    void findStudentsById_notFound() {
        Long id = 999L;

        when(studentRepository.findStudentById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.findStudentsById(id));
    }

    @Test
    @DisplayName("findStudentEntityById - успешно")
    void findStudentEntityById_success() {
        Long id = 1L;
        Student student = new Student();

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));

        Student result = studentService.findStudentEntityById(id);

        assertNotNull(result);
        assertEquals(student, result);
    }

    @Test
    @DisplayName("deleteStudentById — успех и инвалидация кэша")
    void deleteStudent_success() {
        Long id = 1L;
        Student student = new Student();

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));

        studentService.deleteStudentById(id);

        verify(studentRepository).delete(student);
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("deleteStudentById - не найден")
    void deleteStudent_notFound() {
        Long id = 999L;

        when(studentRepository.findStudentById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.deleteStudentById(id));
    }

    @Test
    @DisplayName("assignStudentToRoom - успешно")
    void assignStudentToRoom_success() {
        Long studentId = 1L;
        Long roomId = 1L;

        Room room = new Room();
        room.setId(roomId);
        room.setTotalPlaces(3);
        room.setStudents(new HashSet<>());

        Student student = new Student();
        student.setRoom(null);

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(roomService.findRoomEntityById(roomId)).thenReturn(room);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.assignStudentToRoom(studentId, roomId);

        assertEquals(room, student.getRoom());
        assertTrue(room.getStudents().contains(student));
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("assignStudentToRoom - комната полная")
    void assignStudentToRoom_fullRoom() {
        Long studentId = 1L;
        Long roomId = 1L;

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(2);
        Set<Student> students = new HashSet<>();
        students.add(new Student());
        students.add(new Student());
        room.setStudents(students);

        Student student = new Student();

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(roomService.findRoomEntityById(roomId)).thenReturn(room);

        assertThrows(BadRequestException.class, () -> studentService.assignStudentToRoom(studentId, roomId));
    }

    @Test
    @DisplayName("assignStudentToRoom - уже в этой комнате")
    void assignStudentToRoom_sameRoom() {
        Long studentId = 1L;
        Long roomId = 1L;

        Room room = new Room();
        room.setId(roomId);

        Student student = new Student();
        student.setRoom(room);

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(roomService.findRoomEntityById(roomId)).thenReturn(room);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.assignStudentToRoom(studentId, roomId);

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("removeStudentFromRoom - успешно")
    void removeStudentFromRoom_success() {
        Long studentId = 1L;

        Room room = new Room();
        room.setStudents(new HashSet<>());

        Student student = new Student();
        student.setRoom(room);
        room.getStudents().add(student);

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.removeStudentFromRoom(studentId);

        assertNull(student.getRoom());
        assertFalse(room.getStudents().contains(student));
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("removeStudentFromRoom - студент не в комнате")
    void removeStudentFromRoom_notInRoom() {
        Long studentId = 1L;

        Student student = new Student();
        student.setRoom(null);

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));

        assertThrows(BadRequestException.class, () -> studentService.removeStudentFromRoom(studentId));
    }

    @Test
    @DisplayName("createStudent - успешно")
    void createStudent_success() {
        Long roomId = 1L;
        StudentRequestDto request = new StudentRequestDto();
        request.setName("John");
        request.setSurname("Doe");

        Room room = new Room();
        room.setTotalPlaces(3);
        room.setStudents(new HashSet<>());

        Student student = new Student();

        when(roomService.findRoomEntityById(roomId)).thenReturn(room);
        when(studentMapper.toEntity(request)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.createStudent(roomId, request);

        assertEquals(room, student.getRoom());
        assertTrue(room.getStudents().contains(student));
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("createStudent - комната полная")
    void createStudent_fullRoom() {
        Long roomId = 1L;
        StudentRequestDto request = new StudentRequestDto();

        Room room = new Room();
        room.setNumber(101);
        room.setTotalPlaces(1);
        Set<Student> students = new HashSet<>();
        students.add(new Student());
        room.setStudents(students);

        when(roomService.findRoomEntityById(roomId)).thenReturn(room);

        assertThrows(BadRequestException.class, () -> studentService.createStudent(roomId, request));
    }

    @Test
    @DisplayName("updateStudent - успешно")
    void updateStudent_success() {
        Long id = 1L;
        StudentRequestDto request = new StudentRequestDto();
        request.setName("Updated Name");
        request.setSurname("Updated Surname");
        request.setAge(22);

        Student student = new Student();

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.updateStudent(id, request);

        assertEquals("Updated Name", student.getName());
        assertEquals("Updated Surname", student.getSurname());
        assertEquals(22, student.getAge());
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("updatePatchStudent - успешно")
    void updatePatchStudent_success() {
        Long id = 1L;
        StudentRequestDto request = new StudentRequestDto();
        request.setName("New Name Only");

        Student student = new Student();
        student.setName("Old Name");
        student.setAge(20);

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.updatePatchStudent(id, request);

        assertEquals("New Name Only", student.getName());
        assertEquals(20, student.getAge());
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("creationStudentNoTx - успешно")
    void creationStudentNoTx_success() {
        StudentCreationDto creation = new StudentCreationDto();
        creation.setName("John");
        creation.setSurname("Doe");
        creation.setPatronymic("Smith");
        creation.setPhoneNumber("1234567890");
        creation.setAge(20);
        creation.setChs(100);
        creation.setContractNumber(12345);
        creation.setContractStartDate("2024-01-01");
        creation.setContractEndDate("2024-12-31");
        creation.setRoomId(1L);
        creation.setInitiateProblem(false);

        Room room = new Room();
        room.setStudents(new HashSet<>());

        when(contractRepository.existsByNumber(12345)).thenReturn(false);
        when(roomService.findRoomEntityById(1L)).thenReturn(room);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        StudentResponseDto result = studentService.creationStudentNoTx(creation);

        assertNotNull(result);
        verify(studentRepository, atLeastOnce()).save(any(Student.class));
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    @DisplayName("creationStudentNoTx - дата окончания раньше начала")
    void creationStudentNoTx_invalidDates() {
        StudentCreationDto creation = new StudentCreationDto();
        creation.setContractStartDate("2024-12-31");
        creation.setContractEndDate("2024-01-01");
        creation.setRoomId(1L);
        creation.setContractNumber(12345);

        assertThrows(BadRequestException.class, () -> studentService.creationStudentNoTx(creation));
    }

    @Test
    @DisplayName("creationStudentNoTx - дубликат контракта")
    void creationStudentNoTx_duplicateContract() {
        StudentCreationDto creation = new StudentCreationDto();
        creation.setContractNumber(12345);
        creation.setContractStartDate("2024-01-01");
        creation.setContractEndDate("2024-12-31");
        creation.setRoomId(1L);

        when(contractRepository.existsByNumber(12345)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> studentService.creationStudentNoTx(creation));
    }

    @Test
    @DisplayName("creationStudentWithTx - успешно")
    void creationStudentWithTx_success() {
        StudentCreationDto creation = new StudentCreationDto();
        creation.setName("John");
        creation.setSurname("Doe");
        creation.setPatronymic("Smith");
        creation.setPhoneNumber("1234567890");
        creation.setAge(20);
        creation.setChs(100);
        creation.setContractNumber(12345);
        creation.setContractStartDate("2024-01-01");
        creation.setContractEndDate("2024-12-31");
        creation.setRoomId(1L);
        creation.setInitiateProblem(false);

        Room room = new Room();
        room.setStudents(new HashSet<>());

        when(contractRepository.existsByNumber(12345)).thenReturn(false);
        when(roomService.findRoomEntityById(1L)).thenReturn(room);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> inv.getArgument(0));
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        StudentResponseDto result = studentService.creationStudentWithTx(creation);

        assertNotNull(result);
    }

    @Test
    @DisplayName("assignStudentsToRoom - массовое назначение успешно")
    void assignStudentsToRoom_success() {
        Long roomId = 1L;

        StudentUpdateRequest req1 = new StudentUpdateRequest();
        req1.setId(1L);
        StudentUpdateRequest req2 = new StudentUpdateRequest();
        req2.setId(2L);
        List<StudentUpdateRequest> studentRequests = List.of(req1, req2);

        Room room = new Room();
        room.setId(roomId);
        room.setTotalPlaces(5);
        room.setStudents(new HashSet<>());

        Student student1 = new Student();
        student1.setId(1L);
        Student student2 = new Student();
        student2.setId(2L);

        when(roomService.findRoomEntityById(roomId)).thenReturn(room);
        when(studentRepository.findStudentById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findStudentById(2L)).thenReturn(Optional.of(student2));
        when(studentRepository.saveAll(anyList())).thenReturn(List.of(student1, student2));
        when(studentMapper.toDtoList(anyList())).thenReturn(List.of(new StudentResponseDto(), new StudentResponseDto()));

        List<StudentResponseDto> result = studentService.assignStudentsToRoom(studentRequests, roomId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(room, student1.getRoom());
        assertEquals(room, student2.getRoom());
    }

    @Test
    @DisplayName("assignStudentsToRoom - недостаточно мест")
    void assignStudentsToRoom_insufficientPlaces() {
        Long roomId = 1L;

        StudentUpdateRequest req1 = new StudentUpdateRequest();
        req1.setId(1L);
        StudentUpdateRequest req2 = new StudentUpdateRequest();
        req2.setId(2L);
        List<StudentUpdateRequest> studentRequests = List.of(req1, req2);

        Room room = new Room();
        room.setTotalPlaces(2);
        Set<Student> existingStudents = new HashSet<>();
        existingStudents.add(new Student());
        existingStudents.add(new Student());
        room.setStudents(existingStudents);

        when(roomService.findRoomEntityById(roomId)).thenReturn(room);

        assertThrows(BadRequestException.class, () -> studentService.assignStudentsToRoom(studentRequests, roomId));
    }

    @Test
    @DisplayName("assignViolationsToStudentsNoTx — успех")
    void assignViolationsToStudentsNoTx_success() {
        List<ViolationBulkRequest> requests = new ArrayList<>();
        ViolationBulkRequest req = new ViolationBulkRequest();
        req.setStudentId(1L);
        req.setDate("2024-01-01");
        req.setType(ViolationType.SMOKING);
        requests.add(req);

        Student student = new Student();
        student.setId(1L);
        student.setViolations(new HashSet<>());

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(violationRepository.save(any(Violation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        List<StudentResponseDto> result = studentService.assignViolationsToStudentsNoTx(requests);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(violationRepository, atLeastOnce()).save(any(Violation.class));
    }

    @Test
    @DisplayName("assignViolationsToStudentsNoTx — пустой список")
    void assignViolationsToStudentsNoTx_empty() {
        assertThrows(IllegalArgumentException.class, () -> studentService.assignViolationsToStudentsNoTx(null));
        assertThrows(IllegalArgumentException.class, () -> studentService.assignViolationsToStudentsNoTx(new ArrayList<>()));
    }

    @Test
    @DisplayName("assignViolationsToStudentsNoTx — студент не найден")
    void assignViolationsToStudentsNoTx_studentNotFound() {
        List<ViolationBulkRequest> requests = new ArrayList<>();
        ViolationBulkRequest req = new ViolationBulkRequest();
        req.setStudentId(999L);
        req.setDate("2024-01-01");
        req.setType(ViolationType.SMOKING);
        requests.add(req);

        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.assignViolationsToStudentsNoTx(requests));
    }

    @Test
    @DisplayName("assignViolationsToStudentsWithTx — успех")
    void assignViolationsToStudentsWithTx_success() {
        List<ViolationBulkRequest> requests = new ArrayList<>();
        ViolationBulkRequest req = new ViolationBulkRequest();
        req.setStudentId(1L);
        req.setDate("2024-01-01");
        req.setType(ViolationType.SMOKING);
        requests.add(req);

        Student student = new Student();
        student.setId(1L);
        student.setViolations(new HashSet<>());

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(violationRepository.save(any(Violation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toDto(any(Student.class))).thenReturn(new StudentResponseDto());

        List<StudentResponseDto> result = studentService.assignViolationsToStudentsWithTx(requests);

        assertNotNull(result);
    }

    @Test
    @DisplayName("addViolationToStudent - успешно")
    void addViolationToStudent_success() {
        Long studentId = 1L;
        Long violationId = 1L;

        Student student = new Student();
        student.setViolations(new HashSet<>());

        Violation violation = new Violation();
        violation.setStudents(new HashSet<>());

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(violationService.findViolationById(violationId)).thenReturn(violation);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.addViolationToStudent(studentId, violationId);

        assertTrue(student.getViolations().contains(violation));
        assertTrue(violation.getStudents().contains(student));
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("addViolationToStudent - нарушение уже есть")
    void addViolationToStudent_alreadyExists() {
        Long studentId = 1L;
        Long violationId = 1L;

        Violation violation = new Violation();
        Student student = new Student();
        student.setViolations(new HashSet<>(Set.of(violation)));

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(violationService.findViolationById(violationId)).thenReturn(violation);

        assertThrows(BadRequestException.class, () -> studentService.addViolationToStudent(studentId, violationId));
    }

    @Test
    @DisplayName("removeViolationFromStudent - успешно")
    void removeViolationFromStudent_success() {
        Long studentId = 1L;
        Long violationId = 1L;

        Violation violation = new Violation();
        violation.setStudents(new HashSet<>());

        Student student = new Student();
        student.setViolations(new HashSet<>(Set.of(violation)));

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(violationService.findViolationById(violationId)).thenReturn(violation);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(new StudentResponseDto());

        studentService.removeViolationFromStudent(studentId, violationId);

        assertFalse(student.getViolations().contains(violation));
        assertFalse(violation.getStudents().contains(student));
        verify(cacheManager).invalidate(Student.class);
    }

    @Test
    @DisplayName("removeViolationFromStudent - нарушения нет")
    void removeViolationFromStudent_notFound() {
        Long studentId = 1L;
        Long violationId = 1L;

        Violation violation = new Violation();
        Student student = new Student();
        student.setViolations(new HashSet<>());

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(violationService.findViolationById(violationId)).thenReturn(violation);

        assertThrows(BadRequestException.class, () -> studentService.removeViolationFromStudent(studentId, violationId));
    }

    @Test
    @DisplayName("assignStudentToRoom - перевод из старой комнаты в новую")
    void assignStudentToRoom_changeRoom() {
        Long studentId = 1L;
        Long newRoomId = 2L;

        Room oldRoom = new Room();
        oldRoom.setId(10L);
        oldRoom.setStudents(new HashSet<>());

        Room newRoom = new Room();
        newRoom.setId(newRoomId);
        newRoom.setTotalPlaces(5);
        newRoom.setStudents(new HashSet<>());

        Student student = new Student();
        student.setRoom(oldRoom);
        oldRoom.getStudents().add(student);

        when(studentRepository.findStudentById(studentId)).thenReturn(Optional.of(student));
        when(roomService.findRoomEntityById(newRoomId)).thenReturn(newRoom);
        when(studentMapper.toDto(any())).thenReturn(new StudentResponseDto());

        studentService.assignStudentToRoom(studentId, newRoomId);

        assertFalse(oldRoom.getStudents().contains(student));
        assertTrue(newRoom.getStudents().contains(student));
        assertEquals(newRoom, student.getRoom());
    }

    @Test
    @DisplayName("updatePatchStudent - ошибка: невалидный возраст")
    void updatePatchStudent_invalidAge() {
        Long id = 1L;
        StudentRequestDto request = new StudentRequestDto();
        request.setAge(10); // Слишком молод

        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(new Student()));

        assertThrows(BadRequestException.class, () -> studentService.updatePatchStudent(id, request));
    }

    @Test
    @DisplayName("updatePatchStudent - обновление всех полей сразу")
    void updatePatchStudent_allFields() {
        Long id = 1L;
        StudentRequestDto request = new StudentRequestDto();
        request.setName("NewName");
        request.setSurname("NewSurname");
        request.setPatronymic("NewPatr");
        request.setPhoneNumber("8800");
        request.setAge(20);
        request.setChs(1);

        Student student = new Student();
        when(studentRepository.findStudentById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(any())).thenReturn(new StudentResponseDto());

        studentService.updatePatchStudent(id, request);

        assertEquals("NewName", student.getName());
        assertEquals("NewSurname", student.getSurname());
        assertEquals(20, student.getAge());
    }

    @Test
    @DisplayName("creationStudentNoTx - выброс исключения для проверки транзакции")
    void creationStudentNoTx_triggerException() {
        StudentCreationDto dto = new StudentCreationDto();
        dto.setContractStartDate("2023-01-01");
        dto.setContractEndDate("2023-12-31");
        dto.setInitiateProblem(true); // Активируем ошибку

        when(contractRepository.existsByNumber(any())).thenReturn(false);
        when(roomService.findRoomEntityById(any())).thenReturn(new Room());

        assertThrows(RuntimeException.class, () -> studentService.creationStudentNoTx(dto));
    }

    @Test
    @DisplayName("filterStudentsWithJpqlPaged - ошибка: нет фильтров")
    void filterStudents_noFiltersError() {
        assertThrows(BadRequestException.class, () ->
                studentService.filterStudentsWithJpqlPaged(null, null, 0, 10));

        assertThrows(BadRequestException.class, () ->
                studentService.filterStudentsWithNativePaged(null, null, 0, 10));
    }

    @Test
    @DisplayName("assignStudentsToRoom - успешное массовое назначение и пропуск тех, кто уже в комнате")
    void assignStudentsToRoom_fullCoverage() {
        Long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        room.setTotalPlaces(10);
        room.setStudents(new HashSet<>());

        Student s1 = new Student(); s1.setId(1L); // Новый
        Student s2 = new Student(); s2.setId(2L); s2.setRoom(room); // Уже тут

        StudentUpdateRequest req1 = new StudentUpdateRequest(); req1.setId(1L);
        StudentUpdateRequest req2 = new StudentUpdateRequest(); req2.setId(2L);

        when(roomService.findRoomEntityById(roomId)).thenReturn(room);
        when(studentRepository.findStudentById(1L)).thenReturn(Optional.of(s1));
        when(studentRepository.findStudentById(2L)).thenReturn(Optional.of(s2));

        studentService.assignStudentsToRoom(List.of(req1, req2), roomId);

        assertEquals(room, s1.getRoom());
        verify(studentRepository).saveAll(any());
    }
}