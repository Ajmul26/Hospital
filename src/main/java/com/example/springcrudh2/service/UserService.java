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
        return new UserResponse(savedUser);
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
        
        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(userRequest.getEmail()) 
            && userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + userRequest.getEmail());
        }
        
        existingUser.setName(userRequest.getName());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPhone(userRequest.getPhone());
        
        User updatedUser = userRepository.save(existingUser);
        return new UserResponse(updatedUser);
    }
    
    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
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