package com.example.tradingsimulationsystem;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.FinnhubSymbol;
import com.example.tradingsimulationsystem.dto.StockQuote;
import com.example.tradingsimulationsystem.repository.StockRepository;
import com.example.tradingsimulationsystem.service.FinnhubService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.*;

@EnableScheduling
@SpringBootApplication
public class TradingSimulationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingSimulationSystemApplication.class, args);
    }
    @Bean
    CommandLineRunner seedStocks(StockRepository stockRepository, FinnhubService finnhubService) {
        return args -> {
            // Fetch all US symbols
            List<FinnhubSymbol> allSymbols = finnhubService.getSymbols("US");

            // Limit to 50 for free tier
            List<FinnhubSymbol> selectedSymbols = allSymbols.stream()
                    .limit(50)
                    .toList();

            for (FinnhubSymbol symbol : selectedSymbols) {
                try {
                    StockQuote quote = finnhubService.getQuote(symbol.getSymbol());
                    Stock stock = new Stock(symbol.getSymbol(), quote.getC());
                    stockRepository.save(stock);
                } catch (Exception e) {
                    System.err.println("Failed to fetch quote for " + symbol.getSymbol() + ": " + e.getMessage());
                }
            }
        };
    }

}
