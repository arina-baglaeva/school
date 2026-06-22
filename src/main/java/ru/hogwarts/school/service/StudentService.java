package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long nextId = 1; // счётчик для генерации новых id

    public Student addStudent(Student student) {
        student.setId(nextId++);
        students.put(student.getId(), student);
        return student;
    }

    public Student getStudent(Long id) {
        return students.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public Student updateStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        } else {
            return null;
        }
    }

    public void deleteStudent(Long id) {
        students.remove(id);
    }

    public List<Student> getStudentsByAge(int age) {
        List<Student> result = new ArrayList<>();
        for (Student s : students.values()) {
            if (s.getAge() == age) {
                result.add(s);
            }
        }
        return result;
    }
}