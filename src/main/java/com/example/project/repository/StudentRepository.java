package com.example.project.repository;

import com.example.project.model.Student;
import com.example.project.model.ViolationType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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

}