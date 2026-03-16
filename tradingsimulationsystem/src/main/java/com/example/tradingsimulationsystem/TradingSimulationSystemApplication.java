package com.example.tradingsimulationsystem;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.FinnhubSymbol;
import com.example.tradingsimulationsystem.dto.StockQuote;
import com.example.tradingsimulationsystem.repository.StockRepository;
import com.example.tradingsimulationsystem.service.FinnhubService;
import com.example.tradingsimulationsystem.service.StockDataSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@EnableScheduling

@SpringBootApplication
public class TradingSimulationSystemApplication {

//    private final StockDataSeeder stockDataSeeder;
//
//    public TradingSimulationSystemApplication(StockDataSeeder stockDataSeeder) {
//        this.stockDataSeeder = stockDataSeeder;
//    }

    public static void main(String[] args) {
        SpringApplication.run(TradingSimulationSystemApplication.class, args);
    }

}

