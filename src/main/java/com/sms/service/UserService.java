package com.sms.service;

import com.sms.dto.SignupRequest;
import com.sms.entity.Department;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setDepartment(department);

        return userRepository.save(user);
    }

    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT);
    }

    public List<User> getAllTeachers() {
        return userRepository.findByRole(Role.TEACHER);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateProfile(Long id, String fullName, String email, String phone, Long departmentId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId).orElse(null);
            user.setDepartment(dept);
        }
        return userRepository.save(user);
    }

    public User updateStudent(Long id, String fullName, String email, String phone, Long departmentId) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }
        student.setFullName(fullName);
        student.setEmail(email);
        student.setPhone(phone);
        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId).orElse(null);
            student.setDepartment(dept);
        }
        return userRepository.save(student);
    }

    public void deleteStudent(Long id) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }
        userRepository.delete(student);
    }
}
