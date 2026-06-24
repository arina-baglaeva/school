package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AvatarRepository avatarRepository;

    public StudentService(StudentRepository studentRepository,
                          FacultyRepository facultyRepository,
                          AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.avatarRepository = avatarRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        if (student == null) {
            logger.error("Student is null, cannot create");
            throw new IllegalArgumentException("Student cannot be null");
        }
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        logger.info("Was invoked method for get student by id: {}", id);
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            logger.error("Student with id {} not found", id);
            return null;
        }
        logger.debug("Student with id {} found", id);
        return studentOpt.get();
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }

    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student with id: {}", student.getId());
        if (student == null || student.getId() == null) {
            logger.error("Student or student id is null");
            throw new IllegalArgumentException("Student or student id cannot be null");
        }
        if (!studentRepository.existsById(student.getId())) {
            logger.warn("Student with id {} does not exist, cannot update", student.getId());
            return null;
        }
        Student updated = studentRepository.save(student);
        logger.debug("Student with id {} updated", updated.getId());
        return updated;
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with id: {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("Student with id {} does not exist, cannot delete", id);
            return;
        }
        studentRepository.deleteById(id);
        logger.debug("Student with id {} deleted", id);
    }

    public List<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for get students by age: {}", age);
        List<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for get students by age between {} and {}", minAge, maxAge);
        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range {} - {}", students.size(), minAge, maxAge);
        return students;
    }

    public Integer getStudentCount() {
        logger.info("Was invoked method for get student count");
        Integer count = studentRepository.getStudentCount();
        logger.debug("Total students: {}", count);
        return count;
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for get average age");
        Double avg = studentRepository.getAverageAge();
        if (avg == null) {
            logger.warn("No students found, average age is 0.0");
            return 0.0;
        }
        logger.debug("Average age: {}", avg);
        return avg;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.findLastFiveStudents();
        logger.debug("Found {} last students", students.size());
        return students;
    }

    public Faculty getStudentFaculty(Long studentId) {
        logger.info("Was invoked method for get faculty of student with id: {}", studentId);
        Student student = getStudent(studentId);
        if (student == null) {
            logger.error("Student with id {} not found, cannot get faculty", studentId);
            return null;
        }
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            logger.warn("Student with id {} has no faculty", studentId);
        } else {
            logger.debug("Student with id {} has faculty: {}", studentId, faculty.getName());
        }
        return faculty;
    }
}