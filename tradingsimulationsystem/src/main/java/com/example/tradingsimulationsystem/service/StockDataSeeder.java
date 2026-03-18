package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.QuoteResponse;
import com.example.tradingsimulationsystem.dto.ProfileResponse;
import com.example.tradingsimulationsystem.dto.SymbolResponse;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;

@Service
@Profile("!test")   // Exclude this bean when 'test' profile is active
public class StockDataSeeder {

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
// change the constructor
    public StockDataSeeder(StockRepository stockRepository,
                           @Value("${finnhub.api.key}") String apiKey,
                           @Value("${finnhub.base.url}") String baseUrl) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey != null ? apiKey.trim() : null;
        this.baseUrl = baseUrl != null ? baseUrl.trim() : "https://finnhub.io";

        System.out.println("[StockDataSeeder] API Key injected: " +
                (this.apiKey != null && !this.apiKey.isEmpty()
                        ? "YES (length=" + this.apiKey.length() +
                        ", first5=" + this.apiKey.substring(0, Math.min(5, this.apiKey.length())) + "...)"
                        : "NO - KEY IS NULL OR EMPTY"));
    }

    public String[] fetchSymbolsFromFinnhub(String exchange) {
        String url = baseUrl + "/api/v1/stock/symbol?exchange=" + exchange + "&token=" + apiKey;
        SymbolResponse[] response = restTemplate.getForObject(url, SymbolResponse[].class);
        if (response == null) {
            return new String[0];
        }
        return Arrays.stream(response)
                .map(SymbolResponse::getSymbol)
                .limit(20) // limit to avoid free-tier rate limits
                .toArray(String[]::new);
    }

    public void seedStocks(String[] symbols) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("[StockDataSeeder] ERROR: API key is NULL or EMPTY. Aborting seed.");
            return;
        }

        for (String symbol : symbols) {
            try {
                String quoteUrl = baseUrl + "/api/v1/quote?symbol=" + symbol + "&token=" + apiKey;
                QuoteResponse quote = restTemplate.getForObject(quoteUrl, QuoteResponse.class);

                String profileUrl = baseUrl + "/api/v1/stock/profile2?symbol=" + symbol + "&token=" + apiKey;
                ProfileResponse profile = restTemplate.getForObject(profileUrl, ProfileResponse.class);

                // --- Map all quote fields safely ---
                double current = (quote != null) ? quote.getC() : 0.0;
                double change = (quote != null) ? quote.getD() : 0.0;
                double percentChange = (quote != null) ? quote.getDp() : 0.0;
                double high = (quote != null) ? quote.getH() : 0.0;
                double low = (quote != null) ? quote.getL() : 0.0;
                double open = (quote != null) ? quote.getO() : 0.0;
                double prevClose = (quote != null) ? quote.getPc() : 0.0;

                String description = (profile != null && profile.getName() != null && !profile.getName().isBlank())
                        ? profile.getName()
                        : "Stock for " + symbol;

                int availableQuantity = 10000;

                Stock stock = stockRepository.findBySymbol(symbol).orElse(new Stock());
                stock.setSymbol(symbol);
                stock.setCurrentPrice(current);
                stock.setChange(change);
                stock.setPercentChange(percentChange);
                stock.setHigh(high);
                stock.setLow(low);
                stock.setOpen(open);
                stock.setPreviousClose(prevClose);
                stock.setDisplaySymbol(symbol);
                stock.setDescription(description);
                stock.setAvailableQuantity(availableQuantity);
                stock.setName(profile != null ? profile.getName() : null);

                stockRepository.save(stock);

                System.out.println("Seeded stock " + symbol + " (price=" + current + ", change=" + change + ")");
            } catch (HttpClientErrorException e) {
                System.err.println("[StockDataSeeder] API error for " + symbol + ": " +
                        e.getStatusCode() + " - " + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.err.println("Error seeding " + symbol + ": " + e.getMessage());
            }
        }
    }
}
