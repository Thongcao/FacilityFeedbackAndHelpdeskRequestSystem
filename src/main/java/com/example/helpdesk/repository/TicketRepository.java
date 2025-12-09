package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Ticket entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Facility Helpdesk Team
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find all tickets created by a specific user.
     * 
     * @param userId the user ID to search for
     * @return list of tickets created by the user
     */
    List<Ticket> findByCreatedById(Long userId);

    /**
     * Find all tickets with a specific status.
     * 
     * @param status the status to search for
     * @return list of tickets with the given status
     */
    List<Ticket> findByStatus(String status);

    /**
     * Find all tickets in a specific department.
     * 
     * @param departmentId the department ID to search for
     * @return list of tickets in the department
     */
    List<Ticket> findByDepartmentId(Long departmentId);

    /**
     * Find all tickets in a specific category.
     * 
     * @param categoryId the category ID to search for
     * @return list of tickets in the category
     */
    List<Ticket> findByCategoryId(Long categoryId);
}



