package com.sk.skala.stockapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkalaStockApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkalaStockApiApplication.class, args);
	}

}
