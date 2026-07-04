package com.bank.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RecommendationApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecommendationApplication.class, args);
		System.out.println("========================================");
		System.out.println("  🏦 Star Bank Recommendation Service");
		System.out.println("  ✅ Started successfully!");
		System.out.println("  📍 http://localhost:8080/recommendation/{userId}");
		System.out.println("========================================");
	}
}