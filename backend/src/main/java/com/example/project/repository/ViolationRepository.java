package com.example.project.repository;

import com.example.project.model.Violation;
import com.example.project.model.enums.ViolationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    Optional<Violation> findViolationById(Long id);

    @Query("SELECT DISTINCT v FROM Violation v LEFT JOIN FETCH v.students")
    List<Violation> findAllWithStudents();

    @Query("""
        SELECT DISTINCT v FROM Violation v
        LEFT JOIN FETCH v.students s
        WHERE (:violationType IS NULL OR v.violationType = :violationType)
        AND (:fio IS NULL OR :fio = '' OR
            LOWER(CONCAT(s.surname, ' ', s.name, ' ', s.patronymic)) LIKE LOWER(CONCAT('%', :fio, '%')) OR
            LOWER(CONCAT(s.surname, ' ', s.name)) LIKE LOWER(CONCAT('%', :fio, '%')) OR
            LOWER(s.surname) LIKE LOWER(CONCAT('%', :fio, '%'))
        )
        ORDER BY v.date DESC
    """)
    List<Violation> findAllWithFilters(
            @Param("violationType") ViolationType violationType,
            @Param("fio") String fio
    );
}