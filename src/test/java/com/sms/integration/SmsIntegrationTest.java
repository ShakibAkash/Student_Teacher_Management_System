package com.sms.integration;

import com.sms.dto.SignupRequest;
import com.sms.entity.Course;
import com.sms.entity.Department;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.UserRepository;
import com.sms.service.CourseService;
import com.sms.service.DepartmentService;
import com.sms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SmsIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        // Delete users except the ones seeded by DataInitializer (or delete all safely)
        userRepository.deleteAll();

        // Use existing department seeded by DataInitializer, or create one if empty
        List<Department> existing = departmentRepository.findAll();
        if (!existing.isEmpty()) {
            department = existing.get(0);
        } else {
            department = departmentService.save(new Department(null, "Computer Science", "CS Dept"));
        }
    }

    @Test
    void shouldSignupAndRetrieveStudent() {
        SignupRequest request = new SignupRequest("integstudent", "pass123", "Integration Student",
                "integ@test.com", "1111111111", Role.STUDENT, department.getId());

        User created = userService.signup(request);

        assertNotNull(created.getId());
        assertEquals("integstudent", created.getUsername());
        assertEquals(Role.STUDENT, created.getRole());

        User found = userService.findByUsername("integstudent").orElseThrow();
        assertEquals("Integration Student", found.getFullName());
        assertEquals(department.getId(), found.getDepartment().getId());
    }

    @Test
    void shouldSignupAndRetrieveTeacher() {
        SignupRequest request = new SignupRequest("integteacher", "pass123", "Integration Teacher",
                "teacher@test.com", null, Role.TEACHER, department.getId());

        User created = userService.signup(request);

        assertEquals(Role.TEACHER, created.getRole());

        List<User> teachers = userService.getAllTeachers();
        assertTrue(teachers.stream().anyMatch(t -> t.getUsername().equals("integteacher")));
    }

    @Test
    void shouldNotAllowDuplicateUsername() {
        SignupRequest req1 = new SignupRequest("duplicate", "pass", "User1",
                null, null, Role.STUDENT, null);
        userService.signup(req1);

        SignupRequest req2 = new SignupRequest("duplicate", "pass", "User2",
                null, null, Role.TEACHER, null);

        assertThrows(RuntimeException.class, () -> userService.signup(req2));
    }

    @Test
    void shouldUpdateStudentProfile() {
        SignupRequest request = new SignupRequest("profstudent", "pass", "Original Name",
                null, null, Role.STUDENT, department.getId());
        User user = userService.signup(request);

        User updated = userService.updateProfile(user.getId(), "Updated Name", "new@email.com", "9999999999", department.getId());

        assertEquals("Updated Name", updated.getFullName());
        assertEquals("new@email.com", updated.getEmail());
    }

    @Test
    void teacherShouldEditAndDeleteStudent() {
        SignupRequest request = new SignupRequest("delstudent", "pass", "Delete Me",
                null, null, Role.STUDENT, null);
        User student = userService.signup(request);

        User edited = userService.updateStudent(student.getId(), "Edited Name", "ed@test.com", "0000000000", department.getId());
        assertEquals("Edited Name", edited.getFullName());

        userService.deleteStudent(student.getId());
        assertTrue(userService.findById(student.getId()).isEmpty());
    }

    @Test
    void shouldCrudCourses() {
        SignupRequest tReq = new SignupRequest("courseteacher", "pass", "Course Teacher",
                null, null, Role.TEACHER, department.getId());
        User teacher = userService.signup(tReq);

        Course course = courseService.save("Java 101", "Intro to Java", 3, department.getId(), teacher.getId());
        assertNotNull(course.getId());

        List<Course> courses = courseService.getAllCourses();
        assertTrue(courses.stream().anyMatch(c -> c.getName().equals("Java 101")));

        Course updated = courseService.update(course.getId(), "Java 201", "Advanced Java", 4, department.getId(), teacher.getId());
        assertEquals("Java 201", updated.getName());
        assertEquals(4, updated.getCredits());

        courseService.delete(course.getId());
        assertTrue(courseService.findById(course.getId()).isEmpty());
    }

    @Test
    void shouldManageDepartments() {
        Department d = departmentService.save(new Department(null, "Biology", "Bio Dept"));

        assertNotNull(d.getId());

        List<Department> depts = departmentService.getAllDepartments();
        assertTrue(depts.size() >= 2);

        assertTrue(departmentService.findById(d.getId()).isPresent());
    }
}
