package com.blue;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class BlueLemonApplication {

	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
	public static void main(String[] args) {
		SpringApplication.run(BlueLemonApplication.class, args);
	}

	@Bean
	public ApplicationRunner parameterLogger() {
		return args -> {
			System.out.println("My accessKey value: " + accessKey);
		};
	}
}
