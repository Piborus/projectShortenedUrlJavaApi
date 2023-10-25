package com.example.shorterUrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.shorterUrl")
public class ShorterUrlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShorterUrlApplication.class, args);
	}

}
