package com.sms.controller;

import com.sms.config.TestSecurityConfig;
import com.sms.entity.Course;
import com.sms.entity.Department;
import com.sms.entity.Teacher;
import com.sms.service.CourseService;
import com.sms.service.DepartmentService;
import com.sms.service.TeacherService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@Import(TestSecurityConfig.class)
@DisplayName("CourseController Unit Tests")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private TeacherService teacherService;

    private Course course;
    private Department department;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");

        course = new Course();
        course.setId(1L);
        course.setCode("CS101");
        course.setName("Introduction to Programming");
        course.setCredits(3);
        course.setDepartment(department);
        course.setTeacher(teacher);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should display list of courses")
    void testListCourses() throws Exception {
        // Arrange
        List<Course> courses = Arrays.asList(course);
        when(courseService.getAllCourses()).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/list"))
                .andExpect(model().attribute("courses", hasSize(1)))
                .andExpect(model().attribute("courses", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("code", is("CS101")),
                                hasProperty("name", is("Introduction to Programming"))
                        )
                )));

        verify(courseService).getAllCourses();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should show create course form")
    void testShowCreateForm() throws Exception {
        // Arrange
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(department));
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));

        // Act & Assert
        mockMvc.perform(get("/courses/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/form"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("teachers"));

        verify(departmentService).getAllDepartments();
        verify(teacherService).getAllTeachers();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should create course successfully")
    void testCreateCourse() throws Exception {
        // Arrange
        when(courseService.createCourse(any(Course.class))).thenReturn(course);

        // Act & Assert
        mockMvc.perform(post("/courses")
                        .with(csrf())
                        .param("code", "CS101")
                        .param("name", "Introduction to Programming")
                        .param("credits", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        verify(courseService).createCourse(any(Course.class));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should show edit course form")
    void testShowEditForm() throws Exception {
        // Arrange
        when(courseService.getCourseById(1L)).thenReturn(course);
        when(departmentService.getAllDepartments()).thenReturn(Arrays.asList(department));
        when(teacherService.getAllTeachers()).thenReturn(Arrays.asList(teacher));

        // Act & Assert
        mockMvc.perform(get("/courses/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/form"))
                .andExpect(model().attribute("course", hasProperty("id", is(1L))))
                .andExpect(model().attributeExists("departments"))
                .andExpect(model().attributeExists("teachers"));

        verify(courseService).getCourseById(1L);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should update course successfully")
    void testUpdateCourse() throws Exception {
        // Arrange
        when(courseService.updateCourse(anyLong(), any(Course.class))).thenReturn(course);

        // Act & Assert
        mockMvc.perform(post("/courses/1")
                        .with(csrf())
                        .param("code", "CS101")
                        .param("name", "Advanced Programming")
                        .param("credits", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        verify(courseService).updateCourse(anyLong(), any(Course.class));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should delete course successfully")
    void testDeleteCourse() throws Exception {
        // Arrange
        doNothing().when(courseService).deleteCourse(1L);

        // Act & Assert
        mockMvc.perform(get("/courses/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        verify(courseService).deleteCourse(1L);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should deny course creation for students")
    void testCreateCourse_AccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/courses")
                        .with(csrf())
                        .param("code", "CS101")
                        .param("name", "Test Course")
                        .param("credits", "3"))
                .andExpect(status().isForbidden());

        verify(courseService, never()).createCourse(any(Course.class));
    }
}
