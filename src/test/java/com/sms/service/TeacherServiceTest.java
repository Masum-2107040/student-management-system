package com.sms.service;

import com.sms.entity.Department;
import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService Unit Tests")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;
    private User user;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");

        user = new User();
        user.setId(1L);
        user.setUsername("teacher1");
        user.setPassword("password123");
        user.setRole(User.Role.TEACHER);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setEmployeeId("EMP001");
        teacher.setFirstName("Jane");
        teacher.setLastName("Smith");
        teacher.setEmail("jane.smith@example.com");
        teacher.setPhone("0987654321");
        teacher.setSpecialization("Software Engineering");
        teacher.setDepartment(department);
        teacher.setUser(user);
    }

    @Test
    @DisplayName("Should create teacher successfully")
    void testCreateTeacher_Success() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        // Act
        Teacher createdTeacher = teacherService.createTeacher(teacher);

        // Assert
        assertThat(createdTeacher).isNotNull();
        assertThat(createdTeacher.getUser().getRole()).isEqualTo(User.Role.TEACHER);
        verify(passwordEncoder).encode("password123");
        verify(teacherRepository).save(teacher);
    }

    @Test
    @DisplayName("Should get all teachers successfully")
    void testGetAllTeachers_Success() {
        // Arrange
        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setEmployeeId("EMP002");
        teacher2.setEmail("john.teacher@example.com");
        
        List<Teacher> teachers = Arrays.asList(teacher, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherService.getAllTeachers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(teacher, teacher2);
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should get teacher by ID successfully")
    void testGetTeacherById_Success() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.getTeacherById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
        verify(teacherRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when teacher not found by ID")
    void testGetTeacherById_NotFound() {
        // Arrange
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Teacher result = teacherService.getTeacherById(99L);

        // Assert
        assertThat(result).isNull();
        verify(teacherRepository).findById(99L);
    }

    @Test
    @DisplayName("Should get teacher by email successfully")
    void testGetTeacherByEmail_Success() {
        // Arrange
        when(teacherRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.getTeacherByEmail("jane.smith@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
        verify(teacherRepository).findByEmail("jane.smith@example.com");
    }

    @Test
    @DisplayName("Should get teachers by department successfully")
    void testGetTeachersByDepartment_Success() {
        // Arrange
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findByDepartmentId(1L)).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherService.getTeachersByDepartment(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment().getId()).isEqualTo(1L);
        verify(teacherRepository).findByDepartmentId(1L);
    }

    @Test
    @DisplayName("Should update teacher successfully")
    void testUpdateTeacher_Success() {
        // Arrange
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("John");
        updatedTeacher.setLastName("Doe");
        updatedTeacher.setEmail("john.doe@example.com");
        updatedTeacher.setUser(user);

        when(teacherRepository.existsById(1L)).thenReturn(true);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        // Act
        Teacher result = teacherService.updateTeacher(1L, updatedTeacher);

        // Assert
        assertThat(result).isNotNull();
        verify(teacherRepository).existsById(1L);
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent teacher")
    void testUpdateTeacher_NotFound() {
        // Arrange
        when(teacherRepository.existsById(99L)).thenReturn(false);

        // Act
        Teacher result = teacherService.updateTeacher(99L, teacher);

        // Assert
        assertThat(result).isNull();
        verify(teacherRepository).existsById(99L);
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should delete teacher successfully")
    void testDeleteTeacher_Success() {
        // Arrange
        doNothing().when(teacherRepository).deleteById(1L);

        // Act
        teacherService.deleteTeacher(1L);

        // Assert
        verify(teacherRepository).deleteById(1L);
    }
}
