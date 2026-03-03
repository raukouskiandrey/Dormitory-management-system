package com.example.project.repository;

import com.example.project.model.Student;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {
    private final List<Student> students = new ArrayList<>();

    public StudentRepository() {
        students.add(new Student("Иван", "Иванов", "Иванович", "123-456", 20, 101, 5));
        students.add(new Student("Петр", "Петров", "Петрович", "234-567", 21, 102, 4));
        students.add(new Student("Мария", "Сидорова", "Алексеевна", "345-678", 20, 101, 5));
        students.add(new Student("Анна", "Смирнова", "Дмитриевна", "456-789", 22, 103, 5));
        students.add(new Student("Алексей", "Козлов", "Сергеевич", "567-890", 21, 102, 4));
        students.add(new Student("Елена", "Морозова", "Андреевна", "678-901", 20, 104, 5));
        students.add(new Student("Дмитрий", "Волков", "Николаевич", "789-012", 22, 103, 4));
        students.add(new Student("Ольга", "Павлова", "Викторовна", "890-123", 21, 101, 5));
        students.add(new Student("Андрей", "Соколов", "Игоревич", "901-234", 20, 102, 4));
        students.add(new Student("Татьяна", "Михайлова", "Евгеньевна", "012-345", 22, 104, 5));
    }

    public List<Student> findStudentsByRoom(int number) {
        List<Student> result = new ArrayList<>();
        for (Student current : students) {
            if (number == current.getRoomNumber()) {
                result.add(current);
            }
        }
        return result;
    }

    public List<Student> findStudentsByAge(int age) {
        List<Student> result = new ArrayList<>();
        for (Student current : students) {
            if (current.getAge() == age) {
                result.add(current);
            }
        }
        return result;
    }
}
