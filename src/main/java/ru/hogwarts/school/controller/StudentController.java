package ru.hogwarts.school.controller;

import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    // Внедряем сервис через конструктор (Spring сам подставит)
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // POST /student - создание студента
    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED) // статус 201
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    // GET /student/{id} - получение по id
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    // GET /student - получение всех
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // PUT /student - обновление (полный замен)
    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    // DELETE /student/{id} - удаление
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    // GET /student/age?age=25 - фильтрация по возрасту
    @GetMapping("/age")
    public List<Student> getStudentsByAge(@RequestParam int age) {
        return studentService.getStudentsByAge(age);
    }
}