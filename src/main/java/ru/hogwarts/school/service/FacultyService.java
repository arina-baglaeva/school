package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        if (faculty == null) {
            logger.error("Faculty is null, cannot create");
            throw new IllegalArgumentException("Faculty cannot be null");
        }
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for get faculty by id: {}", id);
        Optional<Faculty> facultyOpt = facultyRepository.findById(id);
        if (facultyOpt.isEmpty()) {
            logger.error("Faculty with id {} not found", id);
            return null;
        }
        logger.debug("Faculty with id {} found", id);
        return facultyOpt.get();
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        List<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty with id: {}", faculty.getId());
        if (faculty == null || faculty.getId() == null) {
            logger.error("Faculty or faculty id is null");
            throw new IllegalArgumentException("Faculty or faculty id cannot be null");
        }
        if (!facultyRepository.existsById(faculty.getId())) {
            logger.warn("Faculty with id {} does not exist, cannot update", faculty.getId());
            return null;
        }
        Faculty updated = facultyRepository.save(faculty);
        logger.debug("Faculty with id {} updated", updated.getId());
        return updated;
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Faculty with id {} does not exist, cannot delete", id);
            return;
        }
        facultyRepository.deleteById(id);
        logger.debug("Faculty with id {} deleted", id);
    }

    public List<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for get faculties by color: {}", color);
        List<Faculty> faculties = facultyRepository.findByColor(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    public List<Faculty> searchFaculties(String query) {
        logger.info("Was invoked method for search faculties by query: {}", query);
        List<Faculty> faculties = facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(query, query);
        logger.debug("Found {} faculties matching query '{}'", faculties.size(), query);
        return faculties;
    }

    public List<Student> getFacultyStudents(Long id) {
        logger.info("Was invoked method for get students of faculty with id: {}", id);
        Faculty faculty = getFaculty(id);
        if (faculty == null) {
            logger.error("Faculty with id {} not found, cannot get students", id);
            return List.of();
        }
        List<Student> students = faculty.getStudents();
        logger.debug("Faculty with id {} has {} students", id, students.size());
        return students;
    }
}