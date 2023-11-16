package com.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AlgamoneyApiApplication {
	
	private static  ApplicationContext APPLICATIONCONTEXT;

	public static void main(String[] args) {
		APPLICATIONCONTEXT = SpringApplication.run(AlgamoneyApiApplication.class, args);
	}

	
	public static <T> T getBean(Class<T> type) {
		return APPLICATIONCONTEXT.getBean(type);
	}
}
