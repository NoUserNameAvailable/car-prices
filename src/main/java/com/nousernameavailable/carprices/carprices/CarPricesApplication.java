package com.nousernameavailable.carprices.carprices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CarPricesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarPricesApplication.class, args);
	}
}
