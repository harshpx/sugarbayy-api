package com.store.sugarbayy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SugarbayyApplication {
	public static void main(String[] args) {
		SpringApplication.run(SugarbayyApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("Sugarbayy API is running!");
		};
	}
}
