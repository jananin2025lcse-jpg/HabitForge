package com.example.habitforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HabitforgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HabitforgeApplication.class, args);

		System.out.println();
		System.out.println("========================================");
		System.out.println("       HABITFORGE STARTED");
		System.out.println("========================================");
		System.out.println("Open in browser:");
		System.out.println("http://localhost:8085/");
		System.out.println("========================================");
	}
}