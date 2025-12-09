package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Student entity.
 * Provides CRUD operations and custom query methods.
 * 
 * @author Facility Helpdesk Team
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find student by student code.
     * 
     * @param studentCode the student code to search for
     * @return Optional containing the student if found, empty otherwise
     */
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Find student by associated user ID.
     * 
     * @param userId the user ID to search for
     * @return Optional containing the student if found, empty otherwise
     */
    Optional<Student> findByUserId(Long userId);
}



