package com.example.tradingsimulationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.GsonBuilderUtils;

@SpringBootApplication
public class TradingsimulationsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingsimulationsystemApplication.class, args);
        System.out.println("Trading app....");
	}
}
