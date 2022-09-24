package com.sparta.ddang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DdangApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(DdangApplication.class, args);
//	}

	public static final String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.properties,"
			+ "classpath:application-alpha.yml,"
			+ "classpath:application-local.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(DdangApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);
	}




}
