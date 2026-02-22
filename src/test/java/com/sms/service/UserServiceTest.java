package com.sms.service;

import com.sms.dto.SignupRequest;
import com.sms.entity.Department;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User student;
    private User teacher;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department(1L, "Computer Science", "CS Dept");

        student = new User();
        student.setId(1L);
        student.setUsername("student1");
        student.setPassword("encoded");
        student.setFullName("Jane Student");
        student.setEmail("jane@test.com");
        student.setPhone("1234567890");
        student.setRole(Role.STUDENT);
        student.setDepartment(department);

        teacher = new User();
        teacher.setId(2L);
        teacher.setUsername("teacher1");
        teacher.setPassword("encoded");
        teacher.setFullName("John Teacher");
        teacher.setEmail("john@test.com");
        teacher.setPhone("0987654321");
        teacher.setRole(Role.TEACHER);
        teacher.setDepartment(department);
    }

    @Test
    void signup_shouldCreateUser() {
        SignupRequest request = new SignupRequest("newuser", "pass", "New User",
                "new@test.com", null, Role.STUDENT, 1L);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded_pass");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.signup(request);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("New User", result.getFullName());
        assertEquals(Role.STUDENT, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_shouldThrowIfUsernameExists() {
        SignupRequest request = new SignupRequest("existing", "pass", "User",
                null, null, Role.STUDENT, null);

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.signup(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllStudents_shouldReturnOnlyStudents() {
        when(userRepository.findByRole(Role.STUDENT)).thenReturn(Arrays.asList(student));

        List<User> students = userService.getAllStudents();

        assertEquals(1, students.size());
        assertEquals(Role.STUDENT, students.get(0).getRole());
    }

    @Test
    void getAllTeachers_shouldReturnOnlyTeachers() {
        when(userRepository.findByRole(Role.TEACHER)).thenReturn(Arrays.asList(teacher));

        List<User> teachers = userService.getAllTeachers();

        assertEquals(1, teachers.size());
        assertEquals(Role.TEACHER, teachers.get(0).getRole());
    }

    @Test
    void findByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(student));

        Optional<User> result = userService.findByUsername("student1");

        assertTrue(result.isPresent());
        assertEquals("student1", result.get().getUsername());
    }

    @Test
    void updateProfile_shouldUpdateFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateProfile(1L, "Updated Name", "updated@test.com", "9999999999", 1L);

        assertEquals("Updated Name", result.getFullName());
        assertEquals("updated@test.com", result.getEmail());
        assertEquals("9999999999", result.getPhone());
    }

    @Test
    void updateProfile_shouldThrowIfNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.updateProfile(99L, "Name", "email", "phone", null));
    }

    @Test
    void updateStudent_shouldUpdateStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateStudent(1L, "Updated Student", "up@test.com", "1111111111", null);

        assertEquals("Updated Student", result.getFullName());
    }

    @Test
    void updateStudent_shouldThrowIfNotStudent() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(teacher));

        assertThrows(RuntimeException.class,
                () -> userService.updateStudent(2L, "Name", "email", "phone", null));
    }

    @Test
    void deleteStudent_shouldDeleteStudent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        userService.deleteStudent(1L);

        verify(userRepository).delete(student);
    }

    @Test
    void deleteStudent_shouldThrowIfNotStudent() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(teacher));

        assertThrows(RuntimeException.class, () -> userService.deleteStudent(2L));
        verify(userRepository, never()).delete(any());
    }
}
