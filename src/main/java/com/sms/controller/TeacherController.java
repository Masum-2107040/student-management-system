package com.sms.controller;

import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.service.DepartmentService;
import com.sms.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    
    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teachers/list";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "teachers/form";
    }
    
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public String createTeacher(@ModelAttribute Teacher teacher, @RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(User.Role.TEACHER);
        teacher.setUser(user);
        teacherService.createTeacher(teacher);
        return "redirect:/teachers";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher != null) {
            model.addAttribute("teacher", teacher);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "teachers/form";
        }
        return "redirect:/teachers";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute Teacher teacher) {
        teacherService.updateTeacher(id, teacher);
        return "redirect:/teachers";
    }
    
    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }
    
    @GetMapping("/{id}")
    public String viewTeacher(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher != null) {
            model.addAttribute("teacher", teacher);
            return "teachers/view";
        }
        return "redirect:/teachers";
    }
}
