package com.example.project.model;

import lombok.Getter;

@Getter
public class Student {
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private int age;
    private int roomNumber;
    private int chs;
    
    public Student(String name, String surname, String patronymic,
                   String phoneNumber, int age, int roomNumber, int chs) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.roomNumber = roomNumber;
        this.chs = chs;
    }
}