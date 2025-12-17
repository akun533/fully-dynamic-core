package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestDataSource {
    public static void main(String[] args) {
        try {
            SpringApplication.run(TestDataSource.class, args);
            System.out.println("Application started successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}