package com.sms.controller;

import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.service.DepartmentService;
import com.sms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    private final DepartmentService departmentService;
    
    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "students/form";
    }
    
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public String createStudent(@ModelAttribute Student student, @RequestParam String username, @RequestParam String password,
                                 RedirectAttributes redirectAttributes) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(User.Role.STUDENT);
        student.setUser(user);
        studentService.createStudent(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
        return "redirect:/students";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student != null) {
            model.addAttribute("student", student);
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "students/form";
        }
        return "redirect:/students";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        studentService.updateStudent(id, student);
        redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        return "redirect:/students";
    }
    
    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        return "redirect:/students";
    }
    
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student != null) {
            model.addAttribute("student", student);
            return "students/view";
        }
        return "redirect:/students";
    }
}
