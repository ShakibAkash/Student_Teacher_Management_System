package com.sms.controller;

import com.sms.entity.User;
import com.sms.service.CourseService;
import com.sms.service.DepartmentService;
import com.sms.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final UserService userService;
    private final CourseService courseService;
    private final DepartmentService departmentService;

    public TeacherController(UserService userService, CourseService courseService,
                             DepartmentService departmentService) {
        this.userService = userService;
        this.courseService = courseService;
        this.departmentService = departmentService;
    }

    @GetMapping("/home")
    public String home() {
        return "teacher/home";
    }

    // --- Students (editable by teacher) ---

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("section", "students");
        return "teacher/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        User student = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("section", "students");
        return "teacher/edit-student";
    }

    @PostMapping("/students/edit/{id}")
    public String updateStudent(@PathVariable Long id,
                                @RequestParam String fullName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) Long departmentId) {
        userService.updateStudent(id, fullName, email, phone, departmentId);
        return "redirect:/teacher/students?updated";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        userService.deleteStudent(id);
        return "redirect:/teacher/students?deleted";
    }

    // --- Courses (editable by teacher) ---

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("teachers", userService.getAllTeachers());
        model.addAttribute("section", "courses");
        return "teacher/courses";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String name,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) Integer credits,
                            @RequestParam(required = false) Long departmentId,
                            @RequestParam(required = false) Long teacherId) {
        courseService.save(name, description, credits, departmentId, teacherId);
        return "redirect:/teacher/courses?added";
    }

    @GetMapping("/courses/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found")));
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("teachers", userService.getAllTeachers());
        model.addAttribute("section", "courses");
        return "teacher/edit-course";
    }

    @PostMapping("/courses/edit/{id}")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) Integer credits,
                               @RequestParam(required = false) Long departmentId,
                               @RequestParam(required = false) Long teacherId) {
        courseService.update(id, name, description, credits, departmentId, teacherId);
        return "redirect:/teacher/courses?updated";
    }

    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.delete(id);
        return "redirect:/teacher/courses?deleted";
    }

    // --- Departments (view only) ---

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("section", "departments");
        return "teacher/departments";
    }

    // --- Teachers (view only) ---

    @GetMapping("/teachers")
    public String teachers(Model model) {
        model.addAttribute("teachers", userService.getAllTeachers());
        model.addAttribute("section", "teachers");
        return "teacher/teachers";
    }

    // --- Profile (editable) ---

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("section", "profile");
        return "teacher/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication authentication,
                                @RequestParam String fullName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) Long departmentId) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userService.updateProfile(user.getId(), fullName, email, phone, departmentId);
        return "redirect:/teacher/profile?updated";
    }
}
