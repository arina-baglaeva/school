package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long nextId = 1;

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(nextId++);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFaculty(Long id) {
        return faculties.get(id);
    }

    public List<Faculty> getAllFaculties() {
        return new ArrayList<>(faculties.values());
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
            return faculty;
        } else {
            return null;
        }
    }

    public void deleteFaculty(Long id) {
        faculties.remove(id);
    }

    // Фильтрация по цвету
    public List<Faculty> getFacultiesByColor(String color) {
        List<Faculty> result = new ArrayList<>();
        for (Faculty f : faculties.values()) {
            if (f.getColor().equals(color)) {
                result.add(f);
            }
        }
        return result;
    }
}