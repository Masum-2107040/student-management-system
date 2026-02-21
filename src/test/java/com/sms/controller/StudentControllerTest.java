package com.sms.controller;

import com.sms.config.TestSecurityConfig;
import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.service.DepartmentService;
import com.sms.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(TestSecurityConfig.class)
@DisplayName("StudentController Unit Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private DepartmentService departmentService;

    private Student student;
    private Department department;
    private List<Student> students;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");

        User user = new User();
        user.setId(1L);
        user.setUsername("student1");
        user.setRole(User.Role.STUDENT);

        student = new Student();
        student.setId(1L);
        student.setStudentId("STU001");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setDepartment(department);
        student.setUser(user);

        students = Arrays.asList(student);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should display list of students")
    void testListStudents() throws Exception {
        // Arrange
        when(studentService.getAllStudents()).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attribute("students", hasSize(1)))
                .andExpect(model().attribute("students", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("firstName", is("John")),
                                hasProperty("lastName", is("Doe"))
                        )
                )));

        verify(studentService).getAllStudents();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should show create student form")
    void testShowCreateForm() throws Exception {
        // Arrange
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(department));

        // Act & Assert
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("departments"));

        verify(departmentService).getAllDepartments();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should create student successfully")
    void testCreateStudent() throws Exception {
        // Arrange
        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        // Act & Assert
        mockMvc.perform(post("/students")
                        .with(csrf())
                        .param("studentId", "STU001")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("username", "student1")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(studentService).createStudent(any(Student.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should deny access to create form for students")
    void testShowCreateForm_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isForbidden());

        verify(departmentService, never()).getAllDepartments();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should show edit student form")
    void testShowEditForm() throws Exception {
        // Arrange
        when(studentService.getStudentById(1L)).thenReturn(student);
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(department));

        // Act & Assert
        mockMvc.perform(get("/students/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attribute("student", hasProperty("id", is(1L))))
                .andExpect(model().attributeExists("departments"));

        verify(studentService).getStudentById(1L);
        verify(departmentService).getAllDepartments();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should redirect when editing non-existent student")
    void testShowEditForm_StudentNotFound() throws Exception {
        // Arrange
        when(studentService.getStudentById(99L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/students/99/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService).getStudentById(99L);
        verify(departmentService, never()).getAllDepartments();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should update student successfully")
    void testUpdateStudent() throws Exception {
        // Arrange
        when(studentService.updateStudent(anyLong(), any(Student.class))).thenReturn(student);

        // Act & Assert
        mockMvc.perform(post("/students/1")
                        .with(csrf())
                        .param("firstName", "Jane")
                        .param("lastName", "Smith")
                        .param("email", "jane.smith@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(studentService).updateStudent(anyLong(), any(Student.class));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should delete student successfully")
    void testDeleteStudent() throws Exception {
        // Arrange
        doNothing().when(studentService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(get("/students/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(studentService).deleteStudent(1L);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should view student details")
    void testViewStudent() throws Exception {
        // Arrange
        when(studentService.getStudentById(1L)).thenReturn(student);

        // Act & Assert
        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/view"))
                .andExpect(model().attribute("student", hasProperty("id", is(1L))));

        verify(studentService).getStudentById(1L);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should redirect when viewing non-existent student")
    void testViewStudent_NotFound() throws Exception {
        // Arrange
        when(studentService.getStudentById(99L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/students/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        verify(studentService).getStudentById(99L);
    }
}
