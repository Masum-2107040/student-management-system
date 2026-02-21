package com.sms.integration;

import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.StudentRepository;
import com.sms.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Student Management Integration Tests")
class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Department department;
    private Student student;

    @BeforeEach
    void setUp() {
        // Clean up
        studentRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // Create test department
        department = new Department();
        department.setName("Computer Science");
        department.setDescription("Computer Science Department");
        department = departmentRepository.save(department);

        // Create test user and student
        User user = new User();
        user.setUsername("teststudent");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(User.Role.STUDENT);
        user = userRepository.save(user);

        student = new Student();
        student.setStudentId("STU001");
        student.setFirstName("Test");
        student.setLastName("Student");
        student.setEmail("test.student@example.com");
        student.setPhone("1234567890");
        student.setDepartment(department);
        student.setUser(user);
        student = studentRepository.save(student);
    }

    @Test
    @Order(1)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should list all students")
    void testListAllStudents() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should create new student with full workflow")
    void testCreateStudentFullWorkflow() throws Exception {
        mockMvc.perform(post("/students")
                        .with(csrf())
                        .param("studentId", "STU002")
                        .param("firstName", "New")
                        .param("lastName", "Student")
                        .param("email", "new.student@example.com")
                        .param("phone", "9876543210")
                        .param("username", "newstudent")
                        .param("password", "newpass123")
                        .param("department.id", String.valueOf(department.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify student was created in database
        Student created = studentRepository.findByEmail("new.student@example.com").orElse(null);
        Assertions.assertNotNull(created);
        Assertions.assertEquals("New", created.getFirstName());
        Assertions.assertEquals("newstudent", created.getUser().getUsername());
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should update existing student")
    void testUpdateStudentFullWorkflow() throws Exception {
        mockMvc.perform(post("/students/" + student.getId())
                        .with(csrf())
                        .param("studentId", student.getStudentId())
                        .param("firstName", "Updated")
                        .param("lastName", "Name")
                        .param("email", student.getEmail())
                        .param("phone", "1111111111")
                        .param("department.id", String.valueOf(department.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify update in database
        Student updated = studentRepository.findById(student.getId()).orElse(null);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("Updated", updated.getFirstName());
        Assertions.assertEquals("1111111111", updated.getPhone());
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should view student details")
    void testViewStudentDetails() throws Exception {
        mockMvc.perform(get("/students/" + student.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("students/view"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("student", hasProperty("email", is(student.getEmail()))));
    }

    @Test
    @Order(5)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should delete student")
    void testDeleteStudentFullWorkflow() throws Exception {
        mockMvc.perform(get("/students/" + student.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        // Verify deletion
        boolean exists = studentRepository.existsById(student.getId());
        Assertions.assertFalse(exists);
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Integration: Student role should not access create form")
    void testStudentRoleAccessControl() throws Exception {
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isForbidden());
    }
}
