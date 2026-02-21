package com.sms.controller;

import com.sms.entity.Department;
import com.sms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @GetMapping
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments/list";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(Model model) {
        model.addAttribute("department", new Department());
        return "departments/form";
    }
    
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public String createDepartment(@ModelAttribute Department department) {
        departmentService.createDepartment(department);
        return "redirect:/departments";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        if (department != null) {
            model.addAttribute("department", department);
            return "departments/form";
        }
        return "redirect:/departments";
    }
    
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department) {
        departmentService.updateDepartment(id, department);
        return "redirect:/departments";
    }
    
    @GetMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/departments";
    }
}
