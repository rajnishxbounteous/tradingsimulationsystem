package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.dto.MarketStatusDTO;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.dto.MarketSummaryDTO;
import com.example.tradingsimulationsystem.dto.NewsDTO;
import com.example.tradingsimulationsystem.service.MarketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private static final Logger logger = LoggerFactory.getLogger(MarketController.class);

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/status")
    public MarketStatusDTO getMarketStatus() {
        logger.info("Market status requested");
        boolean isOpen = marketService.isMarketOpen();
        String message = marketService.getMarketStatusMessage();
        logger.info("Market status retrieved: {}", message);
        return new MarketStatusDTO(isOpen, message);
    }

    @GetMapping("/top-gainers")
    public List<StockDTO> getTopGainers() {
        logger.info("Top gainers of the market requested");
        return marketService.getTopGainers();
    }

    @GetMapping("/top-losers")
    public List<StockDTO> getTopLosers() {
        logger.info("Top losers of the market requested");
        return marketService.getTopLosers();
    }

    @GetMapping("/summary")
    public MarketSummaryDTO getMarketSummary() {
        logger.info("Market summary requested by user.");
        return marketService.getMarketSummary();
    }

    @GetMapping("/news")
    public List<NewsDTO> getMarketNews() {
        logger.info("Market news requested by user");
        return marketService.getMarketNews();
    }
}
