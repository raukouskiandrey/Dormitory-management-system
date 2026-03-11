package com.example.project.repository;

import com.example.project.model.Dormitory;
import com.example.project.model.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select t from Room t")
    @EntityGraph(attributePaths = {"students", "students.violations"})
    List<Room> findAllWithGraph();

    Room findRoomById(Long id);
}
