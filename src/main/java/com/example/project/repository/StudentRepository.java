package com.example.project.repository;

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

    @Query("SELECT s FROM Student s LEFT JOIN s.violations v " +
            "WHERE (:age IS NULL OR s.age = :age) " +
            "AND (:chs IS NULL OR s.chs = :chs) " +
            "AND (:violationType IS NULL OR v.violationType = :violationType)")
    @EntityGraph(attributePaths = {"violations", "room", "contract"})
    Page<Student> findStudentsWithFiltersPaged(
            @Param("age") Integer age,
            @Param("chs") Integer chs,
            @Param("violationType") ViolationType violationType,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT s.* FROM students s " +
            "LEFT JOIN student_violations sv ON s.id = sv.student_id " +
            "LEFT JOIN violations v ON sv.violation_id = v.id " +
            "WHERE (:age IS NULL OR s.age = :age) " +
            "AND (:chs IS NULL OR s.chs = :chs) " +
            "AND (:violationType IS NULL OR v.violation_type = :violationType)",
            countQuery = "SELECT count(DISTINCT s.id) FROM students s " +
                    "LEFT JOIN student_violations sv ON s.id = sv.student_id " +
                    "LEFT JOIN violations v ON sv.violation_id = v.id " +
                    "WHERE (:age IS NULL OR s.age = :age) " +
                    "AND (:chs IS NULL OR s.chs = :chs) " +
                    "AND (:violationType IS NULL OR v.violation_type = :violationType)",
            nativeQuery = true)
    Page<Student> findStudentsByComplexCriteriaNativePaged(
            @Param("age") Integer age,
            @Param("chs") Integer chs,
            @Param("violationType") String violationType,
            Pageable pageable);
}
