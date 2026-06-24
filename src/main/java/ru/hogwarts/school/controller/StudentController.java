package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return student;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        Student updated = studentService.updateStudent(student);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/age")
    public List<Student> getStudentsByAge(@RequestParam int age) {
        return studentService.getStudentsByAge(age);
    }

    @GetMapping("/age-between")
    public List<Student> getStudentsByAgeBetween(@RequestParam int min, @RequestParam int max) {
        return studentService.getStudentsByAgeBetween(min, max);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        return student.getFaculty();
    }

    @GetMapping("/count")
    public Integer getStudentCount() {
        return studentService.getStudentCount();
    }

    @GetMapping("/average-age")
    public Double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getStudentNamesStartingWithA() {
        return studentService.getStudentNamesStartingWithA();
    }

    @GetMapping("/average-age-stream")
    public double getAverageAgeViaStream() {
        return studentService.getAverageAgeViaStream();
    }

    @GetMapping("/sum-parallel")
    public long getSumParallel() {
        return studentService.calculateSumParallel();
    }

    @GetMapping("/print-parallel")
    public void printStudentsParallel() {
        List<Student> students = studentService.getAllStudents();

        int count = Math.min(students.size(), 6);
        List<Student> selected = students.subList(0, count);

        if (selected.size() > 0) System.out.println(selected.get(0).getName());
        if (selected.size() > 1) System.out.println(selected.get(1).getName());

        Thread thread1 = new Thread(() -> {
            if (selected.size() > 2) System.out.println(selected.get(2).getName());
            if (selected.size() > 3) System.out.println(selected.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            if (selected.size() > 4) System.out.println(selected.get(4).getName());
            if (selected.size() > 5) System.out.println(selected.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/print-synchronized")
    public void printStudentsSynchronized() {
        List<Student> students = studentService.getAllStudents();

        int count = Math.min(students.size(), 6);
        List<Student> selected = students.subList(0, count);

        if (selected.size() > 0) printNameSynchronized(selected.get(0).getName());
        if (selected.size() > 1) printNameSynchronized(selected.get(1).getName());

        Thread thread1 = new Thread(() -> {
            if (selected.size() > 2) printNameSynchronized(selected.get(2).getName());
            if (selected.size() > 3) printNameSynchronized(selected.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            if (selected.size() > 4) printNameSynchronized(selected.get(4).getName());
            if (selected.size() > 5) printNameSynchronized(selected.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void printNameSynchronized(String name) {
        System.out.println(name);
    }
}