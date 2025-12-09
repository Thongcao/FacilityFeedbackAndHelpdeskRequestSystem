package com.example.helpdesk.repository;

import com.example.helpdesk.entity.FeedbackCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for FeedbackCategory entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Facility Helpdesk Team
 */
@Repository
public interface FeedbackCategoryRepository extends JpaRepository<FeedbackCategory, Long> {

    /**
     * Find category by name.
     * 
     * @param name the category name to search for
     * @return Optional containing the category if found, empty otherwise
     */
    Optional<FeedbackCategory> findByName(String name);
}



