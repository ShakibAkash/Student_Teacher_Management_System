package com.sms.service;

import com.sms.entity.Course;
import com.sms.entity.Department;
import com.sms.entity.User;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, DepartmentRepository departmentRepository,
                         UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(String name, String description, Integer credits, Long departmentId, Long teacherId) {
        Department dept = null;
        if (departmentId != null) {
            dept = departmentRepository.findById(departmentId).orElse(null);
        }
        User teacher = null;
        if (teacherId != null) {
            teacher = userRepository.findById(teacherId).orElse(null);
        }
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        course.setCredits(credits);
        course.setDepartment(dept);
        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    public Course update(Long id, String name, String description, Integer credits, Long departmentId, Long teacherId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setName(name);
        course.setDescription(description);
        course.setCredits(credits);
        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId).orElse(null);
            course.setDepartment(dept);
        }
        if (teacherId != null) {
            User teacher = userRepository.findById(teacherId).orElse(null);
            course.setTeacher(teacher);
        }
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }
}
