package com.sms.controller;

import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.service.DepartmentService;
import com.sms.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    private final DepartmentService departmentService;
    private final TeacherService teacherService;
    
    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses/list";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "courses/form";
    }
    
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public String createCourse(@ModelAttribute Course course) {
        courseService.createCourse(course);
        return "redirect:/courses";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            model.addAttribute("course", course);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("teachers", teacherService.getAllTeachers());
            return "courses/form";
        }
        return "redirect:/courses";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateCourse(@PathVariable Long id, @ModelAttribute Course course) {
        courseService.updateCourse(id, course);
        return "redirect:/courses";
    }
    
    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }
}
