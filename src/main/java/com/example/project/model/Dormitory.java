package com.example.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dormitories")
@Getter
@Setter
public class Dormitory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @OneToMany(mappedBy = "dormitory",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();
}
