package com.experiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.experiment")
@EnableScheduling
public class WebExperimentApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebExperimentApplication.class, args);
	}
}
