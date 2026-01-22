package com.coomeva.hackathon;

import org.springframework.boot.SpringApplication;

public class TestHackathonApplication {

	public static void main(String[] args) {
		SpringApplication.from(HackathonApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
