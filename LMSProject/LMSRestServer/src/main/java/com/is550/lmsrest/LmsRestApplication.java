package com.is550.lmsrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.is550.lmsrest")
public class LmsRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsRestApplication.class, args);
	}
}
