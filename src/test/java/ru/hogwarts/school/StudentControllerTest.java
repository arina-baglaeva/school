package ru.hogwarts.school;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    private Faculty testFaculty;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testFaculty = new Faculty("Gryffindor", "red");
        testFaculty = facultyRepository.save(testFaculty);

        testStudent = new Student("Harry Potter", 15);
        testStudent.setFaculty(testFaculty);
        testStudent = studentRepository.save(testStudent);
    }

    @AfterEach
    void tearDown() {
        avatarRepository.deleteAll();
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void createStudentTest() {
        Student newStudent = new Student("Hermione", 16);
        newStudent.setFaculty(testFaculty);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                newStudent,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hermione");
        assertThat(response.getBody().getAge()).isEqualTo(16);
    }

    @Test
    void getStudentByIdTest() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + testStudent.getId(),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
    }

    @Test
    void getAllStudentsTest() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student",
                Student[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void updateStudentTest() {
        testStudent.setName("Harry J. Potter");
        testStudent.setAge(16);
        HttpEntity<Student> entity = new HttpEntity<>(testStudent);

        ResponseEntity<Student> response = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                entity,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry J. Potter");
        assertThat(response.getBody().getAge()).isEqualTo(16);
    }

    @Test
    void deleteStudentTest() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/" + testStudent.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRepository.findById(testStudent.getId())).isEmpty();
    }

    @Test
    void getStudentsByAgeTest() {
        Student another = new Student("Ron", 15);
        another.setFaculty(testFaculty);
        studentRepository.save(another);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/age?age=15",
                Student[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getStudentNotFoundTest() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/999",
                Student.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}