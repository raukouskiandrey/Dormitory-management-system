package com.example.project.service;

import com.example.project.cache.CacheKey;
import com.example.project.cache.CacheManager;
import com.example.project.dto.request.StudentCreationDto;
import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.request.StudentUpdateRequest;
import com.example.project.dto.request.ViolationRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.ResourceNotFoundException;
import com.example.project.model.Contract;
import com.example.project.model.Room;
import com.example.project.model.Student;
import com.example.project.model.enums.ViolationType;
import com.example.project.model.Violation;
import com.example.project.mapper.StudentMapper;
import com.example.project.repository.ContractRepository;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentService {
    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 100;
    private static final String STUDENT_NOT_FOUND = "Студент с id не найден:";
    private static final String AGE_VALIDATION_MESSAGE =
            "Возраст студента должен быть в диапазоне от " + MIN_AGE + " до " + MAX_AGE + " лет";

    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;
    private final RoomService roomService;
    private final ContractRepository contractRepository;
    private final CacheManager cacheManager;
    private final ViolationService violationService;
    private final ViolationRepository violationRepository;

    public StudentService(StudentMapper studentMapper,
                          StudentRepository studentRepository,
                          RoomService roomService,
                          ContractRepository contractRepository,
                          CacheManager cacheManager, ViolationService violationService,
                          ViolationRepository violationRepository) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
        this.roomService = roomService;
        this.contractRepository = contractRepository;
        this.cacheManager = cacheManager;
        this.violationService = violationService;
        this.violationRepository = violationRepository;
    }

    public Page<StudentResponseDto> findStudentsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return studentRepository.findAll(pageable)
                .map(studentMapper::toDto);
    }

    public Page<StudentResponseDto> findStudentsByAgePaged(int age, int page, int size) {
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new BadRequestException(AGE_VALIDATION_MESSAGE);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return studentRepository.findByAge(age, pageable)
                .map(studentMapper::toDto);
    }

    public StudentResponseDto findStudentsById(Long id) {
        Student students = studentRepository.findStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
        return studentMapper.toDto(students);
    }

    public Student findStudentEntityById(Long id) {
        return studentRepository.findStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
    }

    public void deleteStudentById(Long id) {
        Student student = studentRepository.findStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
        studentRepository.delete(student);
        cacheManager.invalidate(Student.class);
    }

    public StudentResponseDto assignStudentToRoom(Long studentId, Long roomId) {
        Student student = studentRepository.findStudentById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + studentId));
        Room room = roomService.findRoomEntityById(roomId);

        if (student.getRoom() != null && student.getRoom().getId().equals(roomId)) {
            return studentMapper.toDto(student);
        }

        if (room.getStudents().size() >= room.getTotalPlaces()) {
            throw new BadRequestException("В комнате №" + room.getNumber() + " нет свободных мест");
        }

        if (student.getRoom() != null) {
            student.getRoom().getStudents().remove(student);
        }

        student.setRoom(room);
        room.getStudents().add(student);
        studentRepository.save(student);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentResponseDto removeStudentFromRoom(Long studentId) {
        Student student = studentRepository.findStudentById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + studentId));
        Room room = student.getRoom();
        if (room == null) {
            throw new BadRequestException("Студент с id " + studentId + " и так не числится ни в одной комнате");
        }
        room.getStudents().remove(student);
        student.setRoom(null);
        studentRepository.save(student);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto addViolationToStudent(Long studentId, Long violationId) {
        Student student = studentRepository.findStudentById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + studentId));
        Violation violation = violationService.findViolationById(violationId);

        if (student.getViolations().contains(violation)) {
            throw new BadRequestException("Это нарушение уже есть");
        }

        student.getViolations().add(violation);
        violation.getStudents().add(student);

        studentRepository.save(student);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentResponseDto removeViolationFromStudent(Long studentId, Long violationId) {
        Student student = studentRepository.findStudentById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Студент не найден: " + studentId));

        Violation violation = violationService.findViolationById(violationId);

        if (!student.getViolations().contains(violation)) {
            throw new BadRequestException("У этого студента нет такого нарушения");
        }

        student.getViolations().remove(violation);
        violation.getStudents().remove(student);

        studentRepository.save(student);

        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto createStudent(Long id, StudentRequestDto request) {
        Room room = roomService.findRoomEntityById(id);
        if (room.getStudents().size() >= room.getTotalPlaces()) {
            throw new BadRequestException(
                    "В комнате №" + room.getNumber() + " нет свободных мест (макс. " + room.getTotalPlaces() + ")");
        }
        Student student = studentMapper.toEntity(request);
        student.setRoom(room);
        room.getStudents().add(student);
        studentRepository.save(student);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto studentUpdates) {
        Student student = studentRepository.findStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));
        student.setName(studentUpdates.getName());
        student.setSurname(studentUpdates.getSurname());
        student.setPatronymic(studentUpdates.getPatronymic());
        student.setPhoneNumber(studentUpdates.getPhoneNumber());
        student.setAge(studentUpdates.getAge());
        student.setChs(studentUpdates.getChs());
        studentRepository.save(student);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto updatePatchStudent(Long id, StudentRequestDto studentUpdates) {
        Student student = studentRepository.findStudentById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + id));

        if (studentUpdates.getAge() != null
                && (studentUpdates.getAge() < MIN_AGE || studentUpdates.getAge() > MAX_AGE)) {
            throw new BadRequestException(AGE_VALIDATION_MESSAGE);
        }

        if (studentUpdates.getName() != null) {
            student.setName(studentUpdates.getName());
        }

        if (studentUpdates.getSurname() != null) {
            student.setSurname(studentUpdates.getSurname());
        }

        if (studentUpdates.getPatronymic() != null) {
            student.setPatronymic(studentUpdates.getPatronymic());
        }

        if (studentUpdates.getPhoneNumber() != null) {
            student.setPhoneNumber(studentUpdates.getPhoneNumber());
        }

        if (studentUpdates.getAge() != null) {
            student.setAge(studentUpdates.getAge());
        }

        if (studentUpdates.getChs() != null) {
            student.setChs(studentUpdates.getChs());
        }

        studentRepository.save(student);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto creationStudentNoTx(StudentCreationDto creation) {
        LocalDate start = LocalDate.parse(creation.getContractStartDate());
        LocalDate end = LocalDate.parse(creation.getContractEndDate());

        if (end.isBefore(start)) {
            throw new BadRequestException(
                    "Дата окончания контракта не может быть раньше даты начала");
        }

        if (contractRepository.existsByNumber(creation.getContractNumber())) {
            throw new BadRequestException(
                    "Контракт с номером " + creation.getContractNumber() + " уже существует");
        }

        Room room = roomService.findRoomEntityById(creation.getRoomId());

        Student student = Student.builder()
                .name(creation.getName())
                .surname(creation.getSurname())
                .patronymic(creation.getPatronymic())
                .phoneNumber(creation.getPhoneNumber())
                .age(creation.getAge())
                .chs(creation.getChs())
                .room(room)
                .build();
        studentRepository.save(student);

        if (creation.isInitiateProblem()) {
            throw new BadRequestException("Ошибка для проверки транзакции");
        }

        Contract contract = Contract.builder()
                .number(creation.getContractNumber())
                .startDate(creation.getContractStartDate())
                .endDate(creation.getContractEndDate())
                .student(student)
                .build();
        contractRepository.save(contract);

        student.setContract(contract);
        studentRepository.save(student);

        room.getStudents().add(student);
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentResponseDto creationStudentWithTx(StudentCreationDto creation) {
        return creationStudentNoTx(creation);
    }

    public Page<StudentResponseDto> filterStudentsWithJpqlPaged(
            Integer chs, ViolationType violationType, int page, int size) {
        if (chs == null && violationType == null) {
            throw new BadRequestException(
                    "Необходимо указать хотя бы один фильтр: CHS или тип нарушения");
        }
        CacheKey cacheKey = buildCacheKey(chs, violationType, page, size);

        return cacheManager.computeIfAbsent(cacheKey, () -> {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<Student> studentPage = studentRepository.findStudentsByComplexCriteriaJpql(
                    chs, violationType, pageable);
            return studentPage.map(studentMapper::toDto);
        });
    }

    public Page<StudentResponseDto> filterStudentsWithNativePaged(
            Integer chs, String violationType, int page, int size) {
        if (chs == null && violationType == null) {
            throw new BadRequestException(
                    "Необходимо указать хотя бы один фильтр: CHS или тип нарушения");
        }

        CacheKey cacheKey = buildCacheKey(chs, violationType, page, size);

        return cacheManager.computeIfAbsent(cacheKey, () -> {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            return studentRepository.findStudentsByComplexCriteriaNative(
                    chs, violationType, pageable);
        });
    }

    private CacheKey buildCacheKey(Integer chs, Object violationType, int page, int size) {
        String normalizedViolationType = violationType != null ? violationType.toString() : null;
        return new CacheKey(Student.class, "filterStudents",
                chs, normalizedViolationType, page, size);
    }

    @Transactional
    public List<StudentResponseDto> assignStudentsToRoom(List<StudentUpdateRequest> studentsId, Long roomId) {
        Room room = roomService.findRoomEntityById(roomId);
        int totalAfterAssign = room.getStudents().size() + studentsId.size();
        if (totalAfterAssign > room.getTotalPlaces()) {
            throw new BadRequestException("В комнате недостаточно мест для всей группы. "
                    + "Свободно: " + (room.getTotalPlaces() - room.getStudents().size()));
        }
        List<Student> students = studentsId.stream()
                .map(request -> studentRepository.findStudentById(request.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(STUDENT_NOT_FOUND + request.getId())))
                .toList();
        for (Student student : students) {
            if (student.getRoom() != null && student.getRoom().getId().equals(roomId)) {
                continue;
            }

            if (student.getRoom() != null) {
                student.getRoom().getStudents().remove(student);
            }

            student.setRoom(room);
            room.getStudents().add(student);
        }
        studentRepository.saveAll(students);
        cacheManager.invalidate(Student.class);
        return studentMapper.toDtoList(students);
    }

    public List<StudentResponseDto> assignViolationsToStudentsNoTx(List<ViolationRequestDto> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Список нарушений не может быть пустым");
        }

        Map<Long, Student> studentsCache = new HashMap<>();

        for (ViolationRequestDto dto : requests) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Студент не найден с ID: " + dto.getStudentId()));

            Violation violation = Violation.builder()
                    .date(dto.getDate())
                    .violationType(dto.getViolationType())
                    .students(new HashSet<>(Set.of(student)))
                    .build();

            student.getViolations().add(violation);

            violationRepository.save(violation);
            studentRepository.save(student);

            studentsCache.put(student.getId(), student);
        }

        return studentsCache.values().stream().map(studentMapper::toDto).toList();
    }

    @Transactional
    public List<StudentResponseDto> assignViolationsToStudentsWithTx(List<ViolationRequestDto> requests) {
        return assignViolationsToStudentsNoTx(requests);
    }

    public Page<StudentResponseDto> findStudentsAdvanced(
            Integer chs,
            Boolean hasViolations,
            ViolationType violationType,
            Integer age,
            Long dormitoryId,
            Long roomId,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        // Если "есть нарушения" не выбрано как true,
        // то тип нарушения должен игнорироваться
        ViolationType normalizedViolationType =
                Boolean.TRUE.equals(hasViolations) ? violationType : null;

        Page<Student> studentPage = studentRepository.findAdvanced(
                chs,
                hasViolations,
                normalizedViolationType,
                age,
                dormitoryId,
                roomId,
                pageable
        );

        return studentPage.map(studentMapper::toDto);
    }

    public Page<StudentResponseDto> searchStudentsByFio(String fio, int page, int size) {
        if (fio == null || fio.trim().isEmpty()) {
            throw new BadRequestException("Параметр fio не может быть пустым");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return studentRepository.searchByFio(fio.trim(), pageable)
                .map(studentMapper::toDto);
    }
}
