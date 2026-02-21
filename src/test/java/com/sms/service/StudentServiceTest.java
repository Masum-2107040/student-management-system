package com.sms.service;

import com.sms.entity.Department;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.StudentRepository;
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
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private User user;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");

        user = new User();
        user.setId(1L);
        user.setUsername("student1");
        user.setPassword("password123");
        user.setRole(User.Role.STUDENT);

        student = new Student();
        student.setId(1L);
        student.setStudentId("STU001");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setPhone("1234567890");
        student.setAddress("123 Main St");
        student.setDepartment(department);
        student.setUser(user);
    }

    @Test
    @DisplayName("Should create student successfully")
    void testCreateStudent_Success() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student createdStudent = studentService.createStudent(student);

        // Assert
        assertThat(createdStudent).isNotNull();
        assertThat(createdStudent.getUser().getRole()).isEqualTo(User.Role.STUDENT);
        verify(passwordEncoder).encode("password123");
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should get all students successfully")
    void testGetAllStudents_Success() {
        // Arrange
        Student student2 = new Student();
        student2.setId(2L);
        student2.setStudentId("STU002");
        student2.setEmail("jane.doe@example.com");
        
        List<Student> students = Arrays.asList(student, student2);
        when(studentRepository.findAll()).thenReturn(students);

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(student, student2);
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should get student by ID successfully")
    void testGetStudentById_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        Student result = studentService.getStudentById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when student not found by ID")
    void testGetStudentById_NotFound() {
        // Arrange
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Student result = studentService.getStudentById(99L);

        // Assert
        assertThat(result).isNull();
        verify(studentRepository).findById(99L);
    }

    @Test
    @DisplayName("Should get student by email successfully")
    void testGetStudentByEmail_Success() {
        // Arrange
        when(studentRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(student));

        // Act
        Student result = studentService.getStudentByEmail("john.doe@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(studentRepository).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should get students by department successfully")
    void testGetStudentsByDepartment_Success() {
        // Arrange
        List<Student> students = Arrays.asList(student);
        when(studentRepository.findByDepartmentId(1L)).thenReturn(students);

        // Act
        List<Student> result = studentService.getStudentsByDepartment(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartment().getId()).isEqualTo(1L);
        verify(studentRepository).findByDepartmentId(1L);
    }

    @Test
    @DisplayName("Should update student successfully")
    void testUpdateStudent_Success() {
        // Arrange
        Student updatedStudent = new Student();
        updatedStudent.setFirstName("Jane");
        updatedStudent.setLastName("Smith");
        updatedStudent.setEmail("jane.smith@example.com");
        updatedStudent.setUser(user);

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // Act
        Student result = studentService.updateStudent(1L, updatedStudent);

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository).existsById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent student")
    void testUpdateStudent_NotFound() {
        // Arrange
        when(studentRepository.existsById(99L)).thenReturn(false);

        // Act
        Student result = studentService.updateStudent(99L, student);

        // Assert
        assertThat(result).isNull();
        verify(studentRepository).existsById(99L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void testDeleteStudent_Success() {
        // Arrange
        doNothing().when(studentRepository).deleteById(1L);

        // Act
        studentService.deleteStudent(1L);

        // Assert
        verify(studentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should preserve existing user when updating student without user")
    void testUpdateStudent_PreserveUser() {
        // Arrange
        Student updateData = new Student();
        updateData.setFirstName("Updated");
        updateData.setUser(null);

        when(studentRepository.existsById(1L)).thenReturn(true);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        Student result = studentService.updateStudent(1L, updateData);

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository).save(argThat(s -> s.getUser() != null));
    }
}
