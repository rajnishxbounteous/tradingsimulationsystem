package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.dto.MarketSummaryDTO;
import com.example.tradingsimulationsystem.dto.NewsDTO;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketService {

    private static final Logger logger = LoggerFactory.getLogger(MarketService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.service");

    private final LocalTime marketOpen;
    private final LocalTime marketClose;
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;
    private final String finnhubApiKey;

    public MarketService(
            @Value("${market.open:09:00}") String openTime,
            @Value("${market.close:15:30}") String closeTime,
            StockRepository stockRepository,
            @Value("${finnhub.api.key}") String finnhubApiKey
    ) {
        this.marketOpen = LocalTime.parse(openTime);
        this.marketClose = LocalTime.parse(closeTime);
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
        this.finnhubApiKey = finnhubApiKey;
    }

    // --- Market Status ---
    public boolean isMarketOpen() {
//        LocalTime now = LocalTime.now();
//        boolean open = !now.isBefore(marketOpen) && !now.isAfter(marketClose);
//        logger.debug("Market open check at {}: {}", now, open);
        return true;
    }

    public String getMarketStatusMessage() {
        LocalTime now = LocalTime.now();
        String message;
        if (isMarketOpen()) {
            message = "Market is OPEN (closes at " + marketClose + ")";
        } else if (now.isBefore(marketOpen)) {
            message = "Market is CLOSED (opens at " + marketOpen + ")";
        } else {
            message = "Market is CLOSED (next open tomorrow at " + marketOpen + ")";
        }
        logger.info("Market status message generated: {}", message);
        return message;
    }

    // --- Top Gainers ---
    public List<StockDTO> getTopGainers() {
        logger.info("Fetching top gainers");
        List<StockDTO> gainers = stockRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Stock::getPercentChange).reversed())
                .limit(5)
                .map(StockDTO::fromEntity)
                .collect(Collectors.toList());
        logger.info("Top gainers retrieved: {}", gainers.stream().map(StockDTO::getSymbol).collect(Collectors.toList()));
        return gainers;
    }

    // --- Top Losers ---
    public List<StockDTO> getTopLosers() {
        logger.info("Fetching top losers");
        List<StockDTO> losers = stockRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Stock::getPercentChange))
                .limit(5)
                .map(StockDTO::fromEntity)
                .collect(Collectors.toList());
        logger.info("Top losers retrieved: {}", losers.stream().map(StockDTO::getSymbol).collect(Collectors.toList()));
        return losers;
    }

    // --- Market Summary ---
    public MarketSummaryDTO getMarketSummary() {
        logger.info("Generating market summary");
        List<Stock> stocks = stockRepository.findAll();
        long upCount = stocks.stream().filter(s -> s.getChange() > 0).count();
        long downCount = stocks.stream().filter(s -> s.getChange() < 0).count();
        double avgChange = stocks.stream().mapToDouble(Stock::getPercentChange).average().orElse(0.0);

        MarketSummaryDTO summary = new MarketSummaryDTO(upCount, downCount, avgChange);
        logger.info("Market summary: upCount={}, downCount={}, avgChange={}", upCount, downCount, avgChange);
        return summary;
    }

    // --- Market News (Finnhub general news) ---
    public List<NewsDTO> getMarketNews() {
        logger.info("Fetching market news from Finnhub");
        try {
            String url = "https://finnhub.io/api/v1/news?category=general&token=" + finnhubApiKey;
            NewsDTO[] newsArray = restTemplate.getForObject(url, NewsDTO[].class);
            List<NewsDTO> newsList = List.of(newsArray).stream().limit(5).collect(Collectors.toList());
            logger.info("Retrieved {} market news articles", newsList.size());
            return newsList;
        } catch (Exception e) {
            logger.error("Error fetching market news from Finnhub", e);
            return List.of();
        }
    }
}
