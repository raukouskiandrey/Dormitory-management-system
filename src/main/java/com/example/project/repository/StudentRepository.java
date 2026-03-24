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

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = {"room", "contract"})
    Page<Student> findByAge(int age, Pageable pageable);

    @EntityGraph(attributePaths = {"room", "contract"})
    Page<Student> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"violations", "room", "contract"})
    Student findStudentById(Long id);

    @Query("""
    SELECT s FROM Student s
    WHERE (:chs IS NULL OR s.chs = :chs)
    AND (:violationType IS NULL OR EXISTS (
        SELECT 1 FROM s.violations v WHERE v.violationType = :violationType
    ))
    ORDER BY s.id""")
    @EntityGraph(attributePaths = {"room", "contract"})
    Page<Student> findStudentsWithFiltersJpql(
            @Param("chs") Integer chs,
            @Param("violationType") ViolationType violationType,
            Pageable pageable);

    @Query(value = "SELECT s.id as id, s.name as name, s.surname as surname, "
                + "s.patronymic as patronymic, s.phone_number as phoneNumber, "
                + "s.age as age, s.chs as chs, r.number as roomNumber, d.id as dormitoryId, "
                + "string_agg(DISTINCT v.id::varchar, ', ') as violationIds\n "
                + "FROM students s "
                + "LEFT JOIN rooms r ON s.room_id = r.id "
                + "LEFT JOIN dormitories d ON r.dormitory_id = d.id "
                + "LEFT JOIN student_violations sv ON s.id = sv.student_id "
                + "LEFT JOIN violations v ON sv.violation_id = v.id "
                + "WHERE (:chs IS NULL OR s.chs = :chs) "
                + "AND (:violationType IS NULL OR v.violation_type = CAST(:violationType AS varchar)) "
                + "GROUP BY s.id, s.name, s.surname, s.patronymic, s.phone_number, s.age, s.chs, r.number, d.id "
                + "ORDER BY s.id",
                countQuery = "SELECT COUNT(*) FROM students s",
                nativeQuery = true)
        Page<StudentResponseDto> findStudentsByComplexCriteriaNative(
                @Param("chs") Integer chs,
                @Param("violationType") String violationType,
                Pageable pageable);
}