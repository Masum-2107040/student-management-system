package com.sms.integration;

import com.sms.entity.Course;
import com.sms.entity.Department;
import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.repository.CourseRepository;
import com.sms.repository.DepartmentRepository;
import com.sms.repository.TeacherRepository;
import com.sms.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
@DisplayName("Course Management Integration Tests")
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Department department;
    private Teacher teacher;
    private Course course;

    @BeforeEach
    void setUp() {
        // Clean up
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // Create test department
        department = new Department();
        department.setName("Computer Science");
        department = departmentRepository.save(department);

        // Create test teacher
        User user = new User();
        user.setUsername("testteacher");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(User.Role.TEACHER);
        user = userRepository.save(user);

        teacher = new Teacher();
        teacher.setEmployeeId("EMP001");
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setEmail("test.teacher@example.com");
        teacher.setDepartment(department);
        teacher.setUser(user);
        teacher = teacherRepository.save(teacher);

        // Create test course
        course = new Course();
        course.setCode("CS101");
        course.setName("Introduction to Programming");
        course.setCredits(3);
        course.setDepartment(department);
        course.setTeacher(teacher);
        course = courseRepository.save(course);
    }

    @Test
    @Order(1)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should list all courses")
    void testListAllCourses() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/list"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("courses", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should create new course")
    void testCreateCourseFullWorkflow() throws Exception {
        mockMvc.perform(post("/courses")
                        .with(csrf())
                        .param("code", "CS102")
                        .param("name", "Data Structures")
                        .param("credits", "4")
                        .param("department.id", String.valueOf(department.getId()))
                        .param("teacher.id", String.valueOf(teacher.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        // Verify course was created
        Course created = courseRepository.findByCode("CS102").orElse(null);
        Assertions.assertNotNull(created);
        Assertions.assertEquals("Data Structures", created.getName());
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should update existing course")
    void testUpdateCourseFullWorkflow() throws Exception {
        mockMvc.perform(post("/courses/" + course.getId())
                        .with(csrf())
                        .param("code", course.getCode())
                        .param("name", "Advanced Programming")
                        .param("credits", "4")
                        .param("department.id", String.valueOf(department.getId()))
                        .param("teacher.id", String.valueOf(teacher.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        // Verify update
        Course updated = courseRepository.findById(course.getId()).orElse(null);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("Advanced Programming", updated.getName());
        Assertions.assertEquals(4, updated.getCredits());
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Integration: Should delete course")
    void testDeleteCourseFullWorkflow() throws Exception {
        mockMvc.perform(get("/courses/" + course.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        // Verify deletion
        boolean exists = courseRepository.existsById(course.getId());
        Assertions.assertFalse(exists);
    }
}
