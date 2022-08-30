package com.sparta.ddang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DdangApplication {

	public static void main(String[] args) {
		SpringApplication.run(DdangApplication.class, args);
	}

}
