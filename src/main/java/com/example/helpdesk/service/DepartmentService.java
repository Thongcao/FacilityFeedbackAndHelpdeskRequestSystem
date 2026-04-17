package com.example.helpdesk.service;

import com.example.helpdesk.entity.Department;

import java.util.List;

/**
 * Service interface for Department operations.
 * 
 * @author Facility Helpdesk Team
 */
public interface DepartmentService {
    List<Department> findAll();
}



