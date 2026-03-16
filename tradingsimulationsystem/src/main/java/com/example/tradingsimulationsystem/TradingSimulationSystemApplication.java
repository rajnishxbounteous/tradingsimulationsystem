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

//@Override
//public void run(String... args) {
//    stockDataSeeder.seedStocks(new String[]{"AAPL", "GOOGL", "MSFT"});
//}
//    @Bean
//    CommandLineRunner seedStocks(StockRepository stockRepository, FinnhubService finnhubService) {
//        return args -> {
//            // Avoid reseeding if DB already has data
//            if (stockRepository.count() > 0) {
//                System.out.println("Database already seeded. Skipping initial fetch.");
//                return;
//            }
//
//            System.out.println("Fetching symbols from Finnhub...");
//            List<FinnhubSymbol> allSymbols = finnhubService.getSymbols("US");
//
//            if (allSymbols == null || allSymbols.isEmpty()) {
//                System.err.println("No symbols found. Check API key or connection.");
//                return;
//            }
//
//            // Limit to 30 symbols for free tier safety
//            List<FinnhubSymbol> selectedSymbols = allSymbols.stream()
//                    .limit(30)
//                    .toList();
//
//            System.out.println("Seeding database with 30 stocks...");
//            for (int i = 0; i < selectedSymbols.size(); i++) {
//                FinnhubSymbol sym = selectedSymbols.get(i);
//
//                // Fetch quote only for first 5 to avoid rate limits
//                double initialPrice = 0.0;
//                if (i < 5) {
//                    StockQuote quote = finnhubService.getQuote(sym.getSymbol());
//                    if (quote != null ) {
//                        initialPrice = quote.getC();
//                    }
//                }
//
//                // Use displaySymbol as name if available, else fallback to symbol
//                String name = (sym.getDisplaySymbol() != null && !sym.getDisplaySymbol().isEmpty())
//                        ? sym.getDisplaySymbol()
//                        : sym.getSymbol();
//
//                // Seed with availableQuantity = 0
//                Stock stock = new Stock(
//                        sym.getSymbol(),
//                        initialPrice,
//                        sym.getDisplaySymbol(),
//                        sym.getDescription(),
//                        0,
//                        name
//                );
//
//                stockRepository.save(stock);
//            }
//            System.out.println("Seeding complete.");
//        };


