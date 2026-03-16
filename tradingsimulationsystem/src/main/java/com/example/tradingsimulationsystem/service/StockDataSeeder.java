package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.QuoteResponse;
import com.example.tradingsimulationsystem.dto.ProfileResponse;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StockDataSeeder {

    private static final String API_KEY = "${finnhub.api.key}";
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    public StockDataSeeder(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
    }

    public void seedStocks(String[] symbols) {
        for (String symbol : symbols) {
            try {
                // Fetch quote
                String quoteUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + API_KEY;
                QuoteResponse quote = restTemplate.getForObject(quoteUrl, QuoteResponse.class);

                // Fetch profile
                String profileUrl = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol + "&token=" + API_KEY;
                ProfileResponse profile = restTemplate.getForObject(profileUrl, ProfileResponse.class);

                // Decide availableQuantity (simulation choice)
                int availableQuantity = (int) Math.min(
                        profile != null ? profile.getShareOutstanding() : 1000,
                        1000
                );

                // Create and save stock
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                stock.setDisplaySymbol(symbol);
                stock.setDescription(profile != null ? profile.getName() : symbol);
                stock.setPrice(quote != null ? quote.getC() : 0.0);
                stock.setAvailableQuantity(availableQuantity);

                stockRepository.save(stock);

                System.out.println("Seeded stock: " + symbol +
                        " price=" + (quote != null ? quote.getC() : "N/A") +
                        " qty=" + availableQuantity);
            } catch (Exception e) {
                System.err.println("Error seeding " + symbol + ": " + e.getMessage());
            }
        }
    }
}
