package com.example.springcrudh2.controller;

import com.example.springcrudh2.dto.UserRequest;
import com.example.springcrudh2.dto.UserResponse;
import com.example.springcrudh2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "API for managing users in the system")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * CREATE - Create a new user
     */
    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> createUser(
            @Parameter(description = "User data to create", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        try {
            UserResponse createdUser = userService.createUser(userRequest);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * READ - Get all users
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    /**
     * READ - Get user by ID
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        try {
            UserResponse user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * UPDATE - Update user by ID
     */
    @Operation(summary = "Update user", description = "Updates an existing user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        try {
            UserResponse updatedUser = userService.updateUser(id, userRequest);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            HttpStatus status = e.getMessage().contains("not found") ? 
                HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(error, status);
        }
    }
    
    /**
     * DELETE - Delete user by ID
     */
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * GET user by email
     */
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(
            @Parameter(description = "Email of the user to retrieve", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        try {
            UserResponse user = userService.getUserByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * SEARCH users by name or email
     */
    @Operation(summary = "Search users", description = "Search users by name or email containing the search term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @Parameter(description = "Search term to find users by name or email", required = true, example = "john")
            @RequestParam String term) {
        List<UserResponse> users = userService.searchUsers(term);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    /**
     * GET users by name containing
     */
    @Operation(summary = "Get users by name", description = "Retrieves users whose name contains the given string")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<List<UserResponse>> getUsersByNameContaining(
            @Parameter(description = "Name pattern to search for", required = true, example = "John")
            @PathVariable String name) {
        List<UserResponse> users = userService.getUsersByNameContaining(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    /**
     * CHECK if email exists
     */
    @Operation(summary = "Check if email exists", description = "Checks whether an email address is already registered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email existence status",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(
            @Parameter(description = "Email to check for existence", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * GET total user count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalUserCount() {
        long count = userService.getTotalUserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "User Service");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}