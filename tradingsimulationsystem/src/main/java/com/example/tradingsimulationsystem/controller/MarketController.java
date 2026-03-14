package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.service.MarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    /**
     * Endpoint to check if the market is open.
     * Example: GET /api/market/status
     */
    @GetMapping("/api/market/status")
    public String getMarketStatus() {
        return marketService.getMarketStatusMessage();
    }
}
