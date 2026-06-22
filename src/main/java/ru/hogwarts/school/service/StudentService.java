package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class StudentService {
    // Хранилище студентов: ключ - id, значение - студент
    private final Map<Long, Student> students = new HashMap<>();
    private long nextId = 1; // счётчик для генерации новых id

    // Добавление студента (возвращаем созданного студента с присвоенным id)
    public Student addStudent(Student student) {
        student.setId(nextId++);
        students.put(student.getId(), student);
        return student;
    }

    // Получение студента по id
    public Student getStudent(Long id) {
        return students.get(id);
    }

    // Получение всех студентов
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    // Обновление студента (возвращаем обновлённого)
    public Student updateStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        } else {
            return null; // или можно выбросить исключение, но по заданию пока не требуется
        }
    }

    // Удаление студента
    public void deleteStudent(Long id) {
        students.remove(id);
    }

    // Фильтрация по возрасту
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