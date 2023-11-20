package com.example.sumpdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SumpdataApplication {

	public static void main(String[] args) {
		SpringApplication.run(SumpdataApplication.class, args);
	}

}
