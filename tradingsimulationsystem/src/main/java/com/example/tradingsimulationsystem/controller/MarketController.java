package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.dto.MarketStatusDTO;
import com.example.tradingsimulationsystem.service.MarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
