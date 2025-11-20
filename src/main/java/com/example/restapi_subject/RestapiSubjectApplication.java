package com.example.restapi_subject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableJpaAuditing
public class RestapiSubjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestapiSubjectApplication.class, args);
	}

}
