package com.example.tradingsimulationsystem;

import com.example.tradingsimulationsystem.service.StockDataSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TradingSimulationSystemApplication {

    private final StockDataSeeder stockDataSeeder;

    public TradingSimulationSystemApplication(StockDataSeeder stockDataSeeder) {
        this.stockDataSeeder = stockDataSeeder;
    }

    public static void main(String[] args) {
        SpringApplication.run(TradingSimulationSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedStocksOnStartup() {
        return args -> {
            // Update this list to the symbols you want in the simulation
            String[] symbols = new String[] { "AAPL", "MSFT", "TSLA", "GOOGL", "AMZN" };
            stockDataSeeder.seedStocks(symbols);
        };
    }
}