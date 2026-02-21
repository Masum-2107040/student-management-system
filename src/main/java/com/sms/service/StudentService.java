package com.sms.service;

import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Student createStudent(Student student) {
        if (student.getUser() != null) {
            student.getUser().setPassword(passwordEncoder.encode(student.getUser().getPassword()));
            student.getUser().setRole(User.Role.STUDENT);
        }
        return studentRepository.save(student);
    }
    
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }
    
    public Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email).orElse(null);
    }
    
    public List<Student> getStudentsByDepartment(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId);
    }
    
    public Student updateStudent(Long id, Student student) {
        if (studentRepository.existsById(id)) {
            Student existing = studentRepository.findById(id).get();
            student.setId(id);
            // Preserve user if not updating
            if (student.getUser() == null) {
                student.setUser(existing.getUser());
            } else if (student.getUser().getPassword() != null && !student.getUser().getPassword().isEmpty()) {
                student.getUser().setPassword(passwordEncoder.encode(student.getUser().getPassword()));
            } else {
                student.getUser().setPassword(existing.getUser().getPassword());
            }
            return studentRepository.save(student);
        }
        return null;
    }
    
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
