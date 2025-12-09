package com.example.helpdesk.service;

import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for User management operations.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users.
     * 
     * @return list of all users
     */
    @Override
    public List<User> getAllUsers() {
        logger.info("Retrieving all users");
        return userRepository.findAll();
    }

    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return the user if found
     * @throws IllegalArgumentException if user not found
     */
    @Override
    public User getUserById(Long id) {
        logger.info("Retrieving user with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found with ID: " + id);
                });
    }

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
    @Override
    @Transactional
    public User updateUser(Long userId, String fullName, String email, String role) {
        logger.info("Updating user {}: name={}, email={}, role={}", userId, fullName, email, role);

        User user = getUserById(userId);

        // Validate role
        validateRole(role);

        // Check if email is already taken by another user
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
        }

        // Update user
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (role != null && !role.trim().isEmpty()) {
            user.setRole(role);
        }

        User updatedUser = userRepository.save(user);
        logger.info("User {} updated successfully", userId);

        return updatedUser;
    }

    /**
     * Delete user by ID.
     * Prevents users from deleting their own account.
     * 
     * @param userId the user ID to delete
     * @param currentUserEmail the email of the currently logged-in user
     * @throws IllegalArgumentException if user not found or trying to delete own account
     */
    @Override
    @Transactional
    public void deleteUser(Long userId, String currentUserEmail) {
        logger.info("Deleting user with ID: {} by user: {}", userId, currentUserEmail);

        User user = getUserById(userId);
        
        // Prevent users from deleting their own account
        if (user.getEmail().equals(currentUserEmail)) {
            logger.warn("User {} attempted to delete their own account", currentUserEmail);
            throw new IllegalArgumentException("You cannot delete your own account. Please contact another administrator.");
        }
        
        userRepository.delete(user);

        logger.info("User {} deleted successfully by {}", userId, currentUserEmail);
    }

    /**
     * Change user role (only ADMIN can do this).
     * 
     * @param userId the user ID
     * @param newRole the new role (STUDENT, STAFF, ADMIN)
     * @return the updated user
     * @throws IllegalArgumentException if user not found or invalid role
     */
    @Override
    @Transactional
    public User changeUserRole(Long userId, String newRole) {
        logger.info("Changing user {} role to: {}", userId, newRole);

        validateRole(newRole);
        User user = getUserById(userId);
        user.setRole(newRole);

        User updatedUser = userRepository.save(user);
        logger.info("User {} role changed to: {}", userId, newRole);

        return updatedUser;
    }

    /**
     * Validate user role.
     * 
     * @param role the role to validate
     * @throws IllegalArgumentException if role is invalid
     */
    private void validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        List<String> validRoles = List.of("STUDENT", "STAFF", "ADMIN");
        if (!validRoles.contains(role.toUpperCase())) {
            throw new IllegalArgumentException("Invalid role: " + role + 
                    ". Valid roles are: " + validRoles);
        }
    }
}



