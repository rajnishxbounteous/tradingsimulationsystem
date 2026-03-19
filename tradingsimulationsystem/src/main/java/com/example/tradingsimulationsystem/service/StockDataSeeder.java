package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.QuoteResponse;
import com.example.tradingsimulationsystem.dto.ProfileResponse;
import com.example.tradingsimulationsystem.dto.SymbolResponse;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Service
@Profile("!test")   // Exclude this bean when 'test' profile is active
public class StockDataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(StockDataSeeder.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public StockDataSeeder(StockRepository stockRepository,
                           @Value("${finnhub.api.key}") String apiKey,
                           @Value("${finnhub.base.url}") String baseUrl) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey != null ? apiKey.trim() : null;
        this.baseUrl = baseUrl != null ? baseUrl.trim() : "https://finnhub.io";
    }

    @PostConstruct
    public void logApiKeyStatus() {
        if (hasValidApiKey()) {
            logger.info("API Key injected: YES (length={}, first5={}...)",
                    apiKey.length(),
                    apiKey.substring(0, Math.min(5, apiKey.length())));
        } else {
            logger.warn("API Key injected: NO - KEY IS NULL OR EMPTY");
        }
    }

    public boolean hasValidApiKey() {
        return apiKey != null && !apiKey.isEmpty();
    }

    public String[] fetchSymbolsFromFinnhub(String exchange) {
        logger.info("Fetching symbols from Finnhub for exchange={}", exchange);
        String url = baseUrl + "/api/v1/stock/symbol?exchange=" + exchange + "&token=" + apiKey;
        SymbolResponse[] response = restTemplate.getForObject(url, SymbolResponse[].class);
        if (response == null) {
            logger.warn("No symbols returned from Finnhub for exchange={}", exchange);
            return new String[0];
        }
        String[] symbols = Arrays.stream(response)
                .map(SymbolResponse::getSymbol)
                .limit(20) // limit to avoid free-tier rate limits
                .toArray(String[]::new);
        logger.info("Retrieved {} symbols from Finnhub for exchange={}", symbols.length, exchange);
        return symbols;
    }

    public void seedStocks(String[] symbols) {
        if (!hasValidApiKey()) {
            logger.error("API key is NULL or EMPTY. Aborting seed.");
            return;
        }

        for (String symbol : symbols) {
            try {
                logger.info("Seeding stock data for symbol={}", symbol);

                String quoteUrl = baseUrl + "/api/v1/quote?symbol=" + symbol + "&token=" + apiKey;
                QuoteResponse quote = restTemplate.getForObject(quoteUrl, QuoteResponse.class);

                String profileUrl = baseUrl + "/api/v1/stock/profile2?symbol=" + symbol + "&token=" + apiKey;
                ProfileResponse profile = restTemplate.getForObject(profileUrl, ProfileResponse.class);

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

                logger.info("Seeded stock {} (price={}, change={})", symbol, current, change);
            } catch (HttpClientErrorException e) {
                logger.error("API error for symbol={} : {} - {}",
                        symbol, e.getStatusCode(), e.getResponseBodyAsString());
            } catch (Exception e) {
                logger.error("Error seeding symbol={}: {}", symbol, e.getMessage(), e);
            }
        }
    }
}





//package com.example.tradingsimulationsystem.service;
//
//import com.example.tradingsimulationsystem.domain.Stock;
//import com.example.tradingsimulationsystem.dto.QuoteResponse;
//import com.example.tradingsimulationsystem.dto.ProfileResponse;
//import com.example.tradingsimulationsystem.dto.SymbolResponse;
//import com.example.tradingsimulationsystem.repository.StockRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.util.Arrays;
//
//@Service
//@Profile("!test")   // Exclude this bean when 'test' profile is active
//public class StockDataSeeder {
//
//    private static final Logger logger = LoggerFactory.getLogger(StockDataSeeder.class);
//    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");
//
//    private final StockRepository stockRepository;
//    private final RestTemplate restTemplate;
//    private final String apiKey;
//    private final String baseUrl;
//
//    public StockDataSeeder(StockRepository stockRepository,
//                           @Value("${finnhub.api.key}") String apiKey,
//                           @Value("${finnhub.base.url}") String baseUrl) {
//        this.stockRepository = stockRepository;
//        this.restTemplate = new RestTemplate();
//        this.apiKey = apiKey != null ? apiKey.trim() : null;
//        this.baseUrl = baseUrl != null ? baseUrl.trim() : "https://finnhub.io";
//
//        if (this.apiKey != null && !this.apiKey.isEmpty()) {
//            logger.info("API Key injected: YES (length={}, first5={}...)",
//                    this.apiKey.length(),
//                    this.apiKey.substring(0, Math.min(5, this.apiKey.length())));
//        } else {
//            logger.warn("API Key injected: NO - KEY IS NULL OR EMPTY");
//        }
//    }
//
//    public String[] fetchSymbolsFromFinnhub(String exchange) {
//        logger.info("Fetching symbols from Finnhub for exchange={}", exchange);
//        String url = baseUrl + "/api/v1/stock/symbol?exchange=" + exchange + "&token=" + apiKey;
//        SymbolResponse[] response = restTemplate.getForObject(url, SymbolResponse[].class);
//        if (response == null) {
//            logger.warn("No symbols returned from Finnhub for exchange={}", exchange);
//            return new String[0];
//        }
//        String[] symbols = Arrays.stream(response)
//                .map(SymbolResponse::getSymbol)
//                .limit(20) // limit to avoid free-tier rate limits
//                .toArray(String[]::new);
//        logger.info("Retrieved {} symbols from Finnhub for exchange={}", symbols.length, exchange);
//        return symbols;
//    }
//
//    public void seedStocks(String[] symbols) {
//        if (apiKey == null || apiKey.isEmpty()) {
//            logger.error("API key is NULL or EMPTY. Aborting seed.");
//            return;
//        }
//
//        for (String symbol : symbols) {
//            try {
//                logger.info("Seeding stock data for symbol={}", symbol);
//
//                String quoteUrl = baseUrl + "/api/v1/quote?symbol=" + symbol + "&token=" + apiKey;
//                QuoteResponse quote = restTemplate.getForObject(quoteUrl, QuoteResponse.class);
//
//                String profileUrl = baseUrl + "/api/v1/stock/profile2?symbol=" + symbol + "&token=" + apiKey;
//                ProfileResponse profile = restTemplate.getForObject(profileUrl, ProfileResponse.class);
//
//                // --- Map all quote fields safely ---
//                double current = (quote != null) ? quote.getC() : 0.0;
//                double change = (quote != null) ? quote.getD() : 0.0;
//                double percentChange = (quote != null) ? quote.getDp() : 0.0;
//                double high = (quote != null) ? quote.getH() : 0.0;
//                double low = (quote != null) ? quote.getL() : 0.0;
//                double open = (quote != null) ? quote.getO() : 0.0;
//                double prevClose = (quote != null) ? quote.getPc() : 0.0;
//
//                String description = (profile != null && profile.getName() != null && !profile.getName().isBlank())
//                        ? profile.getName()
//                        : "Stock for " + symbol;
//
//                int availableQuantity = 10000;
//
//                Stock stock = stockRepository.findBySymbol(symbol).orElse(new Stock());
//                stock.setSymbol(symbol);
//                stock.setCurrentPrice(current);
//                stock.setChange(change);
//                stock.setPercentChange(percentChange);
//                stock.setHigh(high);
//                stock.setLow(low);
//                stock.setOpen(open);
//                stock.setPreviousClose(prevClose);
//                stock.setDisplaySymbol(symbol);
//                stock.setDescription(description);
//                stock.setAvailableQuantity(availableQuantity);
//                stock.setName(profile != null ? profile.getName() : null);
//
//                stockRepository.save(stock);
//
//                logger.info("Seeded stock {} (price={}, change={})", symbol, current, change);
//            } catch (HttpClientErrorException e) {
//                logger.error("API error for symbol={} : {} - {}",
//                        symbol, e.getStatusCode(), e.getResponseBodyAsString());
//            } catch (Exception e) {
//                logger.error("Error seeding symbol={}: {}", symbol, e.getMessage(), e);
//            }
//        }
//    }
//}
