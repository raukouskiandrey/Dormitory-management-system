    package com.example.project.model;

    import jakarta.persistence.*;
    import lombok.*;

    import java.util.Set;

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "students")
    public class Student {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String surname;
        private String patronymic;
        private String phoneNumber;
        private Integer age;
        private Integer chs;

        public Student(String name, String surname, String patronymic,
                       String phoneNumber, Integer age, Integer chs) {
            this.name = name;
            this.surname = surname;
            this.patronymic = patronymic;
            this.phoneNumber = phoneNumber;
            this.age = age;
            this.chs = chs;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "room_id")
        private Room room;

        @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
        @JoinColumn(name = "contract_id")
        private Contract contract;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "student_violations",
                joinColumns = @JoinColumn(name = "student_id"),
                inverseJoinColumns = @JoinColumn(name = "violation_id")
        )
        private Set<Violation> violations;

    }