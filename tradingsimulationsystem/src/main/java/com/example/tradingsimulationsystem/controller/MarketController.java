package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.dto.MarketStatusDTO;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.dto.MarketSummaryDTO;
import com.example.tradingsimulationsystem.dto.NewsDTO;
import com.example.tradingsimulationsystem.service.MarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    /**
     * Endpoint to check if the market is open.
     * Example: GET /api/market/status
     */
    @GetMapping("/status")
    public MarketStatusDTO getMarketStatus() {
        boolean isOpen = marketService.isMarketOpen();
        String message = marketService.getMarketStatusMessage();
        return new MarketStatusDTO(isOpen, message);
    }

    /**
     * Endpoint to get top gainers.
     * Example: GET /api/market/top-gainers
     */
    @GetMapping("/top-gainers")
    public List<StockDTO> getTopGainers() {
        return marketService.getTopGainers();
    }

    /**
     * Endpoint to get top losers.
     * Example: GET /api/market/top-losers
     */
    @GetMapping("/top-losers")
    public List<StockDTO> getTopLosers() {
        return marketService.getTopLosers();
    }

    /**
     * Endpoint to get overall market summary.
     * Example: GET /api/market/summary
     */
    @GetMapping("/summary")
    public MarketSummaryDTO getMarketSummary() {
        return marketService.getMarketSummary();
    }

    /**
     * Endpoint to get latest market news.
     * Example: GET /api/market/news
     */
    @GetMapping("/news")
    public List<NewsDTO> getMarketNews() {
        return marketService.getMarketNews();
    }
}
