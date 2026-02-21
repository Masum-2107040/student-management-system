package com.sms.service;

import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Teacher createTeacher(Teacher teacher) {
        if (teacher.getUser() != null) {
            teacher.getUser().setPassword(passwordEncoder.encode(teacher.getUser().getPassword()));
            teacher.getUser().setRole(User.Role.TEACHER);
        }
        return teacherRepository.save(teacher);
    }
    
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
    
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }
    
    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email).orElse(null);
    }
    
    public List<Teacher> getTeachersByDepartment(Long departmentId) {
        return teacherRepository.findByDepartmentId(departmentId);
    }
    
    public Teacher updateTeacher(Long id, Teacher teacher) {
        if (teacherRepository.existsById(id)) {
            Teacher existing = teacherRepository.findById(id).get();
            teacher.setId(id);
            // Preserve user if not updating
            if (teacher.getUser() == null) {
                teacher.setUser(existing.getUser());
            } else if (teacher.getUser().getPassword() != null && !teacher.getUser().getPassword().isEmpty()) {
                teacher.getUser().setPassword(passwordEncoder.encode(teacher.getUser().getPassword()));
            } else {
                teacher.getUser().setPassword(existing.getUser().getPassword());
            }
            return teacherRepository.save(teacher);
        }
        return null;
    }
    
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }
}
