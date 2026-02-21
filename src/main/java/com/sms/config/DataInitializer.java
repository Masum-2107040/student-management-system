package com.sms.config;

import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.service.DepartmentService;
import com.sms.service.StudentService;
import com.sms.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    
    private final DepartmentService departmentService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    
    @Override
    public void run(String... args) throws Exception {
        // Create departments
        if (departmentService.getAllDepartments().isEmpty()) {
            Department cse = new Department();
            cse.setName("Computer Science");
            cse.setDescription("Department of Computer Science and Engineering");
            departmentService.createDepartment(cse);
            
            Department ee = new Department();
            ee.setName("Electrical Engineering");
            ee.setDescription("Department of Electrical Engineering");
            departmentService.createDepartment(ee);
            
            // Create a teacher
            Teacher teacher = new Teacher();
            teacher.setEmployeeId("T001");
            teacher.setFirstName("John");
            teacher.setLastName("Doe");
            teacher.setEmail("teacher@example.com");
            teacher.setPhone("1234567890");
            teacher.setSpecialization("Software Engineering");
            teacher.setDepartment(cse);
            
            User teacherUser = new User();
            teacherUser.setUsername("teacher@example.com");
            teacherUser.setPassword("teacher123");
            teacherUser.setRole(User.Role.TEACHER);
            teacher.setUser(teacherUser);
            
            teacherService.createTeacher(teacher);
            
            // Create a student
            Student student = new Student();
            student.setStudentId("S001");
            student.setFirstName("Jane");
            student.setLastName("Smith");
            student.setEmail("student@example.com");
            student.setPhone("0987654321");
            student.setAddress("123 Main St");
            student.setDepartment(cse);
            
            User studentUser = new User();
            studentUser.setUsername("student@example.com");
            studentUser.setPassword("student123");
            studentUser.setRole(User.Role.STUDENT);
            student.setUser(studentUser);
            
            studentService.createStudent(student);
            
            System.out.println("Sample data initialized successfully!");
        }
    }
}
