package com.sms.service;

import com.sms.entity.Department;
import com.sms.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void getAllDepartments_shouldReturnAll() {
        Department d1 = new Department(1L, "CS", null);
        Department d2 = new Department(2L, "Math", null);

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(2, result.size());
    }

    @Test
    void findById_shouldReturnDepartment() {
        Department d = new Department(1L, "CS", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(d));

        Optional<Department> result = departmentService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("CS", result.get().getName());
    }

    @Test
    void save_shouldSaveDepartment() {
        Department d = new Department(null, "Physics", null);
        when(departmentRepository.save(any(Department.class))).thenAnswer(i -> i.getArgument(0));

        Department result = departmentService.save(d);

        assertNotNull(result);
        assertEquals("Physics", result.getName());
        verify(departmentRepository).save(d);
    }
}
