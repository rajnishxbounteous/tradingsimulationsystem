package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.QuoteResponse;
import com.example.tradingsimulationsystem.dto.ProfileResponse;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class StockDataSeeder {

    private final String apiKey;
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    public StockDataSeeder(StockRepository stockRepository,
                           @Value("${finnhub.api.key}") String apiKey) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    public void seedStocks(String[] symbols) {
        for (String symbol : symbols) {
            try {
                // Fetch quote
                String quoteUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + apiKey;
                QuoteResponse quote = restTemplate.getForObject(quoteUrl, QuoteResponse.class);

                // Fetch profile
                String profileUrl = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol + "&token=" + apiKey;
                ProfileResponse profile = restTemplate.getForObject(profileUrl, ProfileResponse.class);

                // ...
            } catch (Exception e) {
                System.err.println("Error seeding " + symbol + ": " + e.getMessage());
            }
        }
    }
}