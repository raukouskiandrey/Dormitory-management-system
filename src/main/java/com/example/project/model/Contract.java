package com.example.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;
    private String startDate;
    private String endDate;

    @OneToOne(mappedBy = "contract",cascade = CascadeType.ALL)
    private Student student;
}
