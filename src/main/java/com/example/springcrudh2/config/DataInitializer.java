package com.example.springcrudh2.config;

import com.example.springcrudh2.entity.User;
import com.example.springcrudh2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() == 0) {
            // Create sample users
            User user1 = new User("John Doe", "john.doe@example.com", "+1234567890");
            User user2 = new User("Jane Smith", "jane.smith@example.com", "+0987654321");
            User user3 = new User("Bob Johnson", "bob.johnson@example.com", "+1122334455");
            User user4 = new User("Alice Brown", "alice.brown@example.com", "+5566778899");
            User user5 = new User("Charlie Wilson", "charlie.wilson@example.com", "+9988776655");
            
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            userRepository.save(user5);
            
            System.out.println("Sample data initialized successfully!");
        }
    }
}