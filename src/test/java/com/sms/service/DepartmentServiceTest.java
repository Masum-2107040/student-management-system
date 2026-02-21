package com.sms.service;

import com.sms.entity.Department;
import com.sms.repository.DepartmentRepository;
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
@DisplayName("DepartmentService Unit Tests")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Computer Science");
        department.setDescription("Department of Computer Science");
    }

    @Test
    @DisplayName("Should create department successfully")
    void testCreateDepartment_Success() {
        // Arrange
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        // Act
        Department createdDepartment = departmentService.createDepartment(department);

        // Assert
        assertThat(createdDepartment).isNotNull();
        assertThat(createdDepartment.getName()).isEqualTo("Computer Science");
        verify(departmentRepository).save(department);
    }

    @Test
    @DisplayName("Should get all departments successfully")
    void testGetAllDepartments_Success() {
        // Arrange
        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("Mathematics");
        
        List<Department> departments = Arrays.asList(department, department2);
        when(departmentRepository.findAll()).thenReturn(departments);

        // Act
        List<Department> result = departmentService.getAllDepartments();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(department, department2);
        verify(departmentRepository).findAll();
    }

    @Test
    @DisplayName("Should get department by ID successfully")
    void testGetDepartmentById_Success() {
        // Arrange
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        // Act
        Department result = departmentService.getDepartmentById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Computer Science");
        verify(departmentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return null when department not found by ID")
    void testGetDepartmentById_NotFound() {
        // Arrange
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Department result = departmentService.getDepartmentById(99L);

        // Assert
        assertThat(result).isNull();
        verify(departmentRepository).findById(99L);
    }

    @Test
    @DisplayName("Should update department successfully")
    void testUpdateDepartment_Success() {
        // Arrange
        Department updatedDepartment = new Department();
        updatedDepartment.setName("Computer Science & Engineering");
        updatedDepartment.setDescription("Updated description");

        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        // Act
        Department result = departmentService.updateDepartment(1L, updatedDepartment);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Computer Science & Engineering");
        verify(departmentRepository).existsById(1L);
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent department")
    void testUpdateDepartment_NotFound() {
        // Arrange
        when(departmentRepository.existsById(99L)).thenReturn(false);

        // Act
        Department result = departmentService.updateDepartment(99L, department);

        // Assert
        assertThat(result).isNull();
        verify(departmentRepository).existsById(99L);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    @DisplayName("Should delete department successfully")
    void testDeleteDepartment_Success() {
        // Arrange
        doNothing().when(departmentRepository).deleteById(1L);

        // Act
        departmentService.deleteDepartment(1L);

        // Assert
        verify(departmentRepository).deleteById(1L);
    }
}
