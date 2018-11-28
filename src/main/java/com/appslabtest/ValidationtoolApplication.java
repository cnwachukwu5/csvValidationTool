package com.appslabtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Make the application deployable by extending SpringBootServletInitializer
 * and override configure
 */
@SpringBootApplication
public class ValidationtoolApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ValidationtoolApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(ValidationtoolApplication.class);
	}
}
