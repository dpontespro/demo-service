package com.dpontespro.devops;

import com.dpontespro.devops.configuration.AsynchronousConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
public class DpsProDemoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DpsProDemoServiceApplication.class, args);
	}

}
