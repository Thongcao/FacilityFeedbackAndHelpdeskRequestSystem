package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Department entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Facility Helpdesk Team
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Find department by name.
     * 
     * @param name the department name to search for
     * @return Optional containing the department if found, empty otherwise
     */
    Optional<Department> findByName(String name);
}



