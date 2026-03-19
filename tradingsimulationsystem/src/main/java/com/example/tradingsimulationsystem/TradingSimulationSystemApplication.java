package com.example.tradingsimulationsystem;

import com.example.tradingsimulationsystem.service.StockDataSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TradingSimulationSystemApplication {

    private static final Logger logger = LoggerFactory.getLogger(TradingSimulationSystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TradingSimulationSystemApplication.class, args);
    }

    /**
     * Seed stocks on startup, but only when NOT running in the 'test' profile.
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner seedStocksOnStartup(StockDataSeeder stockDataSeeder) {
        return args -> {
            if (stockDataSeeder.hasValidApiKey()) {
                logger.info("Starting stock seeding on application startup...");
                String[] symbols = stockDataSeeder.fetchSymbolsFromFinnhub("US");
                stockDataSeeder.seedStocks(symbols);
                logger.info("Stock seeding completed.");
            } else {
                logger.warn("Skipping stock seeding: API key missing");
            }
        };
    }
}





//package com.example.tradingsimulationsystem;
//
//import com.example.tradingsimulationsystem.service.StockDataSeeder;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//@EnableScheduling
//@SpringBootApplication
//public class TradingSimulationSystemApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(TradingSimulationSystemApplication.class, args);
//    }
//
//    /**
//     * Seed stocks on startup, but only when NOT running in the 'test' profile.
//     */
//    @Bean
//    @Profile("!test")   // <--- Runner excluded in test profile
//    public CommandLineRunner seedStocksOnStartup(StockDataSeeder stockDataSeeder) {
//        return args -> {
//            String[] symbols = stockDataSeeder.fetchSymbolsFromFinnhub("US");
//            stockDataSeeder.seedStocks(symbols);
//        };
//    }
//}
