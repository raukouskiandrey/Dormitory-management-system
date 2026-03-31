package com.example.project.repository;

import com.example.project.model.Dormitory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory, Long> {

    @Query("select t from Dormitory t")
    @EntityGraph(attributePaths = {"rooms", "rooms.students", "rooms.students.violations"})
    List<Dormitory> findAllWithGraph();

    List<Dormitory> findAll();

    Optional<Dormitory> findDormitoryById(Long id);

    boolean existsByNameAndAddress(String name,String addres);
}