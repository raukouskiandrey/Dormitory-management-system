package com.example.project.repository;


import com.example.project.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    Violation findViolationById(Long id);
}
