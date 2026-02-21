package com.sms.service;

import com.sms.entity.Course;
import com.sms.entity.Department;
import com.sms.entity.Teacher;
import com.sms.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

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
    @DisplayName("Should create course successfully")
    void testCreateCourse_Success() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course createdCourse = courseService.createCourse(course);

        // Assert
        assertThat(createdCourse).isNotNull();
        assertThat(createdCourse.getCode()).isEqualTo("CS101");
        assertThat(createdCourse.getName()).isEqualTo("Introduction to Programming");
        verify(courseRepository).save(course);
    }

    @Test
    @DisplayName("Should get all courses successfully")
    void testGetAllCourses_Success() {
        // Arrange
        Course course2 = new Course();
        course2.setId(2L);
        course2.setCode("CS102");
        course2.setName("Data Structures");
        
        List<Course> courses = Arrays.asList(course, course2);
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(course, course2);
        verify(courseRepository).findAll();
    }

    @Test
    @DisplayName("Should get course by ID successfully")
    void testGetCourseById_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // Act
        Course result = courseService.getCourseById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("CS101");
        verify(courseRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when course not found by ID")
    void testGetCourseById_NotFound() {
        // Arrange
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Course result = courseService.getCourseById(99L);

        // Assert
        assertThat(result).isNull();
        verify(courseRepository).findById(99L);
    }

    @Test
    @DisplayName("Should get courses by department successfully")
    void testGetCoursesByDepartment_Success() {
        // Arrange
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findByDepartmentId(1L)).thenReturn(courses);

        // Act
        List<Course> result = courseService.getCoursesByDepartment(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment().getId()).isEqualTo(1L);
        verify(courseRepository).findByDepartmentId(1L);
    }

    @Test
    @DisplayName("Should get courses by teacher successfully")
    void testGetCoursesByTeacher_Success() {
        // Arrange
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findByTeacherId(1L)).thenReturn(courses);

        // Act
        List<Course> result = courseService.getCoursesByTeacher(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeacher().getId()).isEqualTo(1L);
        verify(courseRepository).findByTeacherId(1L);
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdateCourse_Success() {
        // Arrange
        Course updatedCourse = new Course();
        updatedCourse.setCode("CS101");
        updatedCourse.setName("Advanced Programming");
        updatedCourse.setCredits(4);

        when(courseRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // Act
        Course result = courseService.updateCourse(1L, updatedCourse);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Advanced Programming");
        verify(courseRepository).existsById(1L);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent course")
    void testUpdateCourse_NotFound() {
        // Arrange
        when(courseRepository.existsById(99L)).thenReturn(false);

        // Act
        Course result = courseService.updateCourse(99L, course);

        // Assert
        assertThat(result).isNull();
        verify(courseRepository).existsById(99L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void testDeleteCourse_Success() {
        // Arrange
        doNothing().when(courseRepository).deleteById(1L);

        // Act
        courseService.deleteCourse(1L);

        // Assert
        verify(courseRepository).deleteById(1L);
    }
}
