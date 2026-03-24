package com.example.project.repository;

import com.example.project.dto.response.StudentResponseDto;
import com.example.project.model.Student;
import com.example.project.model.ViolationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByRoomNumber(int roomNumber);

    List<Student> findByAge(int age);

    List<Student> findByViolationsViolationType(ViolationType type);

    @EntityGraph(attributePaths = {"violations", "room", "contract"})
    List<Student> findAll();

    @EntityGraph(attributePaths = {"violations", "room", "contract"})
    Student findStudentById(Long id);


    @Query("SELECT new com.example.project.dto.response.StudentResponseDto("
            + "s.id, s.name, s.surname, s.patronymic, s.phoneNumber, s.age, s.chs, "
            + "r.number, d.id) "
            + "FROM Student s "
            + "LEFT JOIN s.room r "
            + "LEFT JOIN r.dormitory d "
            + "WHERE (:chs IS NULL OR s.chs = :chs) "
            + "AND (:violationType IS NULL OR EXISTS "
            + "    (SELECT v FROM s.violations v WHERE v.violationType = :violationType))")
    Page<StudentResponseDto> findStudentsWithFiltersPaged(
            @Param("chs") Integer chs,
            @Param("violationType") ViolationType violationType,
            Pageable pageable);

    @Query(value = "SELECT s.id, s.name, s.surname, s.patronymic, "
            + "s.phone_number, s.age, s.chs, "
            + "r.number as room_number, d.id as dormitory_id "
            + "FROM students s "
            + "LEFT JOIN rooms r ON s.room_id = r.id "
            + "LEFT JOIN dormitories d ON r.dormitory_id = d.id "
            + "WHERE (:chs IS NULL OR s.chs = :chs) "
            + "AND (:violationType IS NULL OR EXISTS ( "
            + "    SELECT 1 FROM student_violations sv "
            + "    JOIN violations v ON sv.violation_id = v.id "
            + "    WHERE sv.student_id = s.id AND v.violation_type = CAST(:violationType AS varchar) "
            + "))",
            countQuery = "SELECT COUNT(*) FROM students s "
                    + "WHERE (:chs IS NULL OR s.chs = :chs) "
                    + "AND (:violationType IS NULL OR EXISTS ( "
                    + "    SELECT 1 FROM student_violations sv "
                    + "    JOIN violations v ON sv.violation_id = v.id "
                    + "    WHERE sv.student_id = s.id AND v.violation_type = CAST(:violationType AS varchar) "
                    + "))",
            nativeQuery = true)
    Page<StudentResponseDto> findStudentsByComplexCriteriaNativePaged(
            @Param("chs") Integer chs,
            @Param("violationType") String violationType,
            Pageable pageable);
}
