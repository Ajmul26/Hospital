package com.example.springcrudh2;

import com.example.springcrudh2.dto.UserRequest;
import com.example.springcrudh2.dto.UserResponse;
import com.example.springcrudh2.entity.User;
import com.example.springcrudh2.repository.UserRepository;
import com.example.springcrudh2.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb-test",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class SpringCrudH2ApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertThat(userService).isNotNull();
        assertThat(userRepository).isNotNull();
    }
    
    @Test
    void testCreateUser() throws Exception {
        UserRequest userRequest = new UserRequest("Test User", "test@example.com", "+1234567890");
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"));
    }
    
    @Test
    void testGetAllUsers() throws Exception {
        // Create a test user
        User user = new User("Test User", "test@example.com", "+1234567890");
        userRepository.save(user);
        
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void testGetUserById() throws Exception {
        // Create a test user
        User user = new User("Test User", "test@example.com", "+1234567890");
        User savedUser = userRepository.save(user);
        
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
    
    @Test
    void testUpdateUser() throws Exception {
        // Create a test user
        User user = new User("Test User", "test@example.com", "+1234567890");
        User savedUser = userRepository.save(user);
        
        UserRequest updateRequest = new UserRequest("Updated User", "updated@example.com", "+0987654321");
        
        mockMvc.perform(put("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }
    
    @Test
    void testDeleteUser() throws Exception {
        // Create a test user
        User user = new User("Test User", "test@example.com", "+1234567890");
        User savedUser = userRepository.save(user);
        
        mockMvc.perform(delete("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
    
    @Test
    void testSearchUsers() throws Exception {
        // Create test users
        User user1 = new User("John Doe", "john@example.com", "+1234567890");
        User user2 = new User("Jane Smith", "jane@example.com", "+0987654321");
        userRepository.save(user1);
        userRepository.save(user2);
        
        mockMvc.perform(get("/api/users/search?term=john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("User Service"));
    }
}