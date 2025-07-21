package com.example.springcrudh2.repository;

import com.example.springcrudh2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
    
    // Find users by name containing (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Find users by phone
    Optional<User> findByPhone(String phone);
    
    // Custom query to find users by name or email
    @Query("SELECT u FROM User u WHERE u.name LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    List<User> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
    
    // Count users by name containing
    long countByNameContainingIgnoreCase(String name);
}