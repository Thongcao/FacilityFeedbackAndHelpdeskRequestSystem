package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Staff entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Facility Helpdesk Team
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /**
     * Find staff by associated user ID.
     * 
     * @param userId the user ID to search for
     * @return Optional containing the staff if found, empty otherwise
     */
    Optional<Staff> findByUserId(Long userId);
}



