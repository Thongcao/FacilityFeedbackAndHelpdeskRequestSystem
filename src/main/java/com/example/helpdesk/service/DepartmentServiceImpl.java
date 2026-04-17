package com.example.helpdesk.service;

import com.example.helpdesk.entity.Department;
import com.example.helpdesk.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Department operations.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }
}








