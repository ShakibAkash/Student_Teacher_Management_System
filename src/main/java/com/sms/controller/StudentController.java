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
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final CourseService courseService;
    private final DepartmentService departmentService;

    public StudentController(UserService userService, CourseService courseService,
                             DepartmentService departmentService) {
        this.userService = userService;
        this.courseService = courseService;
        this.departmentService = departmentService;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("section", "home");
        return "student/home";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("section", "courses");
        return "student/courses";
    }

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("section", "departments");
        return "student/departments";
    }

    @GetMapping("/teachers")
    public String teachers(Model model) {
        model.addAttribute("teachers", userService.getAllTeachers());
        model.addAttribute("section", "teachers");
        return "student/teachers";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("section", "profile");
        return "student/profile";
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
        return "redirect:/student/profile?updated";
    }
}
