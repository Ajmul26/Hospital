package com.example.springcrudh2.service;

import com.example.springcrudh2.dto.UserRequest;
import com.example.springcrudh2.dto.UserResponse;
import com.example.springcrudh2.entity.User;
import com.example.springcrudh2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SplunkService splunkService;
    
    /**
     * Create a new user
     */
    public UserResponse createUser(UserRequest userRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + userRequest.getEmail());
        }
        
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        
        User savedUser = userRepository.save(user);
        UserResponse response = new UserResponse(savedUser);
        
        // Log user creation to Splunk
        try {
            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("user_name", savedUser.getName());
            details.put("user_phone", savedUser.getPhone());
            details.put("created_at", savedUser.getCreatedAt());
            
            splunkService.sendUserEvent("CREATE", savedUser.getId(), savedUser.getEmail(), details);
        } catch (Exception e) {
            // Don't fail the operation if Splunk logging fails
            System.err.println("Failed to log user creation to Splunk: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return new UserResponse(user);
    }
    
    /**
     * Update user by ID
     */
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Store original values for logging
        String originalName = existingUser.getName();
        String originalEmail = existingUser.getEmail();
        String originalPhone = existingUser.getPhone();
        
        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(userRequest.getEmail()) 
            && userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + userRequest.getEmail());
        }
        
        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhone(userRequest.getPhone());
        
        User updatedUser = userRepository.save(existingUser);
        UserResponse response = new UserResponse(updatedUser);
        
        // Log user update to Splunk
        try {
            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("original_name", originalName);
            details.put("new_name", updatedUser.getName());
            details.put("original_email", originalEmail);
            details.put("new_email", updatedUser.getEmail());
            details.put("original_phone", originalPhone);
            details.put("new_phone", updatedUser.getPhone());
            details.put("updated_at", updatedUser.getUpdatedAt());
            
            splunkService.sendUserEvent("UPDATE", updatedUser.getId(), updatedUser.getEmail(), details);
        } catch (Exception e) {
            System.err.println("Failed to log user update to Splunk: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        // Get user details before deletion for logging
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        String deletedUserName = userToDelete.getName();
        String deletedUserEmail = userToDelete.getEmail();
        
        userRepository.deleteById(id);
        
        // Log user deletion to Splunk
        try {
            java.util.Map<String, Object> details = new java.util.HashMap<>();
            details.put("deleted_user_name", deletedUserName);
            details.put("deleted_user_phone", userToDelete.getPhone());
            details.put("deleted_at", java.time.Instant.now().toString());
            details.put("original_created_at", userToDelete.getCreatedAt());
            
            splunkService.sendUserEvent("DELETE", id, deletedUserEmail, details);
        } catch (Exception e) {
            System.err.println("Failed to log user deletion to Splunk: " + e.getMessage());
        }
    }
    
    /**
     * Get user by email
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return new UserResponse(user);
    }
    
    /**
     * Search users by name or email
     */
    public List<UserResponse> searchUsers(String searchTerm) {
        return userRepository.searchByNameOrEmail(searchTerm)
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get users by name containing (case insensitive)
     */
    public List<UserResponse> getUsersByNameContaining(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Get total user count
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }
}