package com.example.paymentsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Payments Service application.
 */
@SpringBootApplication
@EnableScheduling
public class PaymentsServiceApplication {

	/**
	 * Main method to run the application.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(PaymentsServiceApplication.class, args);
	}

}
