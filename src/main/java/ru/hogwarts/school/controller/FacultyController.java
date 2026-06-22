package ru.hogwarts.school.controller;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @PutMapping
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @DeleteMapping("/{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    // GET /faculty/color?color=красный - фильтрация по цвету
    @GetMapping("/color")
    public List<Faculty> getFacultiesByColor(@RequestParam String color) {
        return facultyService.getFacultiesByColor(color);
    }
}