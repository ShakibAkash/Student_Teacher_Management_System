package com.sms.service;

import com.sms.entity.Department;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(DepartmentRepository departmentRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            Department cs = new Department(null, "Computer Science", "CS Department");
            Department math = new Department(null, "Mathematics", "Math Department");
            Department phy = new Department(null, "Physics", "Physics Department");
            departmentRepository.save(cs);
            departmentRepository.save(math);
            departmentRepository.save(phy);
        }

        if (userRepository.count() == 0) {
            Department cs = departmentRepository.findAll().get(0);

            User teacher = new User();
            teacher.setUsername("teacher1");
            teacher.setPassword(passwordEncoder.encode("password"));
            teacher.setFullName("John Teacher");
            teacher.setEmail("teacher@sms.com");
            teacher.setPhone("1234567890");
            teacher.setRole(Role.TEACHER);
            teacher.setDepartment(cs);
            userRepository.save(teacher);

            User student = new User();
            student.setUsername("student1");
            student.setPassword(passwordEncoder.encode("password"));
            student.setFullName("Jane Student");
            student.setEmail("student@sms.com");
            student.setPhone("0987654321");
            student.setRole(Role.STUDENT);
            student.setDepartment(cs);
            userRepository.save(student);
        }
    }
}
