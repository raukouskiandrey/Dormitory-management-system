package com.example.project.repository;

import com.example.project.model.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select t from Room t")
    @EntityGraph(attributePaths = {"students", "students.violations"})
    List<Room> findAllWithGraph();

    Optional<Room> findRoomById(Long id);

    boolean existsByNumberAndDormitoryId(Integer number,Long dormitoryId);
}
