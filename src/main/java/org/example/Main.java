package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@RestController
@EnableJpaRepositories(basePackages = {"org.example", "org.example.repository.dynamic"})
@ComponentScan(basePackages = {"org.example", "org.example.controller", "org.example.service", "org.example.generator", "org.example.config"})
public class Main {
    
    @Autowired
    private UserRepository userRepository;
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("")
    public String hello() {
        return "Hello and welcome!";
    }
    
    @GetMapping("/test-db")
    public String testDBConnection() {
        try {
            long count = userRepository.count();
            return "Database connection successful! Number of users: " + count;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
    
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}