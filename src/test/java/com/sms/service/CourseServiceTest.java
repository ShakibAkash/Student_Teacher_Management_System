package com.sms.service;

import com.sms.entity.Course;
import com.sms.entity.Department;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private Department department;
    private User teacher;

    @BeforeEach
    void setUp() {
        department = new Department(1L, "CS", null);

        teacher = new User();
        teacher.setId(1L);
        teacher.setFullName("Teacher");
        teacher.setRole(Role.TEACHER);

        course = new Course(1L, "Java 101", "Intro to Java", 3, department, teacher);
    }

    @Test
    void getAllCourses_shouldReturnAllCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        List<Course> courses = courseService.getAllCourses();

        assertEquals(1, courses.size());
        assertEquals("Java 101", courses.get(0).getName());
    }

    @Test
    void findById_shouldReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Java 101", result.get().getName());
    }

    @Test
    void save_shouldCreateCourse() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        Course result = courseService.save("New Course", "Desc", 4, 1L, 1L);

        assertNotNull(result);
        assertEquals("New Course", result.getName());
        assertEquals(4, result.getCredits());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void update_shouldUpdateCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        Course result = courseService.update(1L, "Updated", "Updated desc", 5, 1L, 1L);

        assertEquals("Updated", result.getName());
        assertEquals(5, result.getCredits());
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> courseService.update(99L, "Name", "Desc", 3, null, null));
    }

    @Test
    void delete_shouldDeleteCourse() {
        courseService.delete(1L);
        verify(courseRepository).deleteById(1L);
    }
}
