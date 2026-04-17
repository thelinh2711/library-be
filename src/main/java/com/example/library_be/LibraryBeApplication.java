package com.example.library_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibraryBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryBeApplication.class, args);
	}
	// in realase/v1.2.0
}
