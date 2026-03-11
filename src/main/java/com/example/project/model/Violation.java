package com.example.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "violations")
@Getter
@Setter
public class Violation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type")
    private ViolationType violationType;

    private String date;

    @ManyToMany(mappedBy = "violations",fetch = FetchType.LAZY)
    List<Student> students = new ArrayList<>();
}
