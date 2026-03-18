package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.dto.MarketSummaryDTO;
import com.example.tradingsimulationsystem.dto.NewsDTO;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketService {

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
        return true;
//        LocalTime now = LocalTime.now();
//        return !now.isBefore(marketOpen) && !now.isAfter(marketClose);
    }

    public String getMarketStatusMessage() {
        LocalTime now = LocalTime.now();
        if (isMarketOpen()) {
            return "Market is OPEN (closes at " + marketClose + ")";
        } else if (now.isBefore(marketOpen)) {
            return "Market is CLOSED (opens at " + marketOpen + ")";
        } else {
            return "Market is CLOSED (next open tomorrow at " + marketOpen + ")";
        }
    }

    // --- Top Gainers ---
    public List<StockDTO> getTopGainers() {
        return stockRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Stock::getPercentChange).reversed())
                .limit(5)
                .map(StockDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Top Losers ---
    public List<StockDTO> getTopLosers() {
        return stockRepository.findAll().stream()
                .sorted(Comparator.comparingDouble(Stock::getPercentChange))
                .limit(5)
                .map(StockDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Market Summary ---
    public MarketSummaryDTO getMarketSummary() {
        List<Stock> stocks = stockRepository.findAll();
        long upCount = stocks.stream().filter(s -> s.getChange() > 0).count();
        long downCount = stocks.stream().filter(s -> s.getChange() < 0).count();
        double avgChange = stocks.stream().mapToDouble(Stock::getPercentChange).average().orElse(0.0);

        return new MarketSummaryDTO(upCount, downCount, avgChange);
    }

    // --- Market News (Finnhub general news) ---
    public List<NewsDTO> getMarketNews() {
        String url = "https://finnhub.io/api/v1/news?category=general&token=" + finnhubApiKey;
        NewsDTO[] newsArray = restTemplate.getForObject(url, NewsDTO[].class);
        return List.of(newsArray).stream().limit(5).collect(Collectors.toList());
    }
}
