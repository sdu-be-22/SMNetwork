package com.example.learnspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InNews {

	public static void main(String[] args) {
		SpringApplication.run(InNews.class, args);
	}

}
