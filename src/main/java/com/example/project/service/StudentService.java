package com.example.project.service;

import com.example.project.dto.request.StudentCreationDto;
import com.example.project.dto.request.StudentRequestDto;
import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.Contract;
import com.example.project.model.Room;
import com.example.project.model.Student;
import com.example.project.model.ViolationType;
import com.example.project.model.Violation;
import com.example.project.mapper.StudentMapper;
import com.example.project.repository.ContractRepository;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.ViolationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;
    private final RoomService roomService;
    private final ContractRepository contractRepository;
    private final ViolationRepository violationRepository;

    public StudentService(StudentMapper studentMapper,
                          StudentRepository studentRepository,
                          RoomService roomService,
                          ContractRepository contractRepository,
                          ViolationRepository violationRepository) {
        this.studentMapper = studentMapper;
        this.studentRepository = studentRepository;
        this.roomService = roomService;
        this.contractRepository = contractRepository;
        this.violationRepository = violationRepository;
    }

    public List<StudentResponseDto> findStudentsByRoom(int number) {
        List<Student> students = studentRepository.findByRoomNumber(number);
        return studentMapper.toDtoList(students);
    }

    public List<StudentResponseDto> findStudentsByAge(int age) {
        List<Student> students = studentRepository.findByAge(age);
        return studentMapper.toDtoList(students);
    }

    public List<StudentResponseDto> findByViolationsViolationType(ViolationType type) {
        List<Student> students = studentRepository.findByViolationsViolationType(type);
        return studentMapper.toDtoList(students);
    }

    public List<StudentResponseDto> findStudents() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.toDtoList(students);
    }

    public StudentResponseDto findStudentsById(Long id) {
        Student students = studentRepository.findStudentById(id);
        return studentMapper.toDto(students);
    }

    public Student findStudentEntityById(Long id) {
        return studentRepository.findStudentById(id);
    }

    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    public StudentResponseDto assignStudentToRoom(Long studentId, Long roomId) {
        Student student = studentRepository.findStudentById(studentId);
        Room room = roomService.findRoomEntityById(roomId);

        if (student.getRoom() != null) {
            student.getRoom().getStudents().remove(student);
        }

        student.setRoom(room);
        room.getStudents().add(student);
        studentRepository.save(student);

        return studentMapper.toDto(student);
    }

    public StudentResponseDto addViolationToStudent(Long studentId, Long violationId) {
        Student student = studentRepository.findStudentById(studentId);
        Violation violation = violationRepository.findViolationById(violationId);

        if (student.getViolations().contains(violation)) {
            throw new RuntimeException("Это нарушение уже есть");
        }

        student.getViolations().add(violation);
        violation.getStudents().add(student);

        studentRepository.save(student);

        return studentMapper.toDto(student);
    }

    public StudentResponseDto createStudent(Long id, StudentRequestDto request) {
        Room room = roomService.findRoomEntityById(id);
        Student student = studentMapper.toEntity(request);

        student.setRoom(room);
        room.getStudents().add(student);
        studentRepository.save(student);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto updateStudent(Long id, StudentRequestDto studentUpdates) {
        Student student = studentRepository.findStudentById(id);

        student.setName(studentUpdates.getName());
        student.setSurname(studentUpdates.getSurname());
        student.setPatronymic(studentUpdates.getPatronymic());
        student.setPhoneNumber(studentUpdates.getPhoneNumber());
        student.setAge(studentUpdates.getAge());
        student.setChs(studentUpdates.getChs());
        studentRepository.save(student);
        return studentMapper.toDto(student);
    }

    public StudentResponseDto updatePatchStudent(Long id, StudentRequestDto studentUpdates) {
        Student student = studentRepository.findStudentById(id);

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
        return studentMapper.toDto(student);
    }

    public StudentResponseDto creationStudentNoTx(StudentCreationDto creation) {
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
            throw new RuntimeException("Ошибка");
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
}