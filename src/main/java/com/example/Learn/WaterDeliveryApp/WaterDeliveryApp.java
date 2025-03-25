package com.example.Learn.WaterDeliveryApp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WaterDeliveryApp {
	private static final Logger logger = LoggerFactory.getLogger(WaterDeliveryApp.class);

	public static void main(String[] args) {
		SpringApplication.run(WaterDeliveryApp.class, args);
	}

}