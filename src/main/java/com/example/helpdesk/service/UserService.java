package com.example.helpdesk.service;

import com.example.helpdesk.entity.User;

import java.util.List;

/**
 * Service interface for User management operations.
 * 
 * @author Facility Helpdesk Team
 */
public interface UserService {

    /**
     * Get all users.
     * 
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return the user if found
     * @throws IllegalArgumentException if user not found
     */
    User getUserById(Long id);

    /**
     * Update user information.
     * 
     * @param userId the user ID
     * @param fullName the new full name
     * @param email the new email
     * @param role the new role
     * @return the updated user
     * @throws IllegalArgumentException if user not found or invalid data
     */
    User updateUser(Long userId, String fullName, String email, String role);

    /**
     * Delete user by ID.
     * Prevents users from deleting their own account.
     * 
     * @param userId the user ID to delete
     * @param currentUserEmail the email of the currently logged-in user
     * @throws IllegalArgumentException if user not found or trying to delete own account
     */
    void deleteUser(Long userId, String currentUserEmail);

    /**
     * Change user role (only ADMIN can do this).
     * 
     * @param userId the user ID
     * @param newRole the new role (STUDENT, STAFF, ADMIN)
     * @return the updated user
     * @throws IllegalArgumentException if user not found or invalid role
     */
    User changeUserRole(Long userId, String newRole);
}



