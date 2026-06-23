package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createStudentTest() throws Exception {
        Student student = new Student("Hermione", 16);
        student.setId(1L);
        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hermione"));
    }

    @Test
    void getStudentByIdTest() throws Exception {
        Student student = new Student("Harry", 15);
        student.setId(1L);
        when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry"));
    }

    @Test
    void getAllStudentsTest() throws Exception {
        Student s1 = new Student("Harry", 15);
        s1.setId(1L);
        Student s2 = new Student("Hermione", 16);
        s2.setId(2L);
        when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateStudentTest() throws Exception {
        Student updated = new Student("Harry J. Potter", 16);
        updated.setId(1L);
        when(studentService.updateStudent(any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry J. Potter"));
    }

    @Test
    void deleteStudentTest() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentsByAgeTest() throws Exception {
        Student s1 = new Student("Harry", 15);
        s1.setId(1L);
        Student s2 = new Student("Ron", 15);
        s2.setId(2L);
        when(studentService.getStudentsByAge(15)).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/student/age?age=15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getStudentNotFoundTest() throws Exception {
        when(studentService.getStudent(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }
}