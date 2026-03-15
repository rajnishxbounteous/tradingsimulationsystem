package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.service.PortfolioService;
import com.example.tradingsimulationsystem.service.ReportingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reporting")
public class ReportingController {

    private final ReportingService reportingService;
    private final PortfolioService portfolioService;

    public ReportingController(ReportingService reportingService,
                               PortfolioService portfolioService) {
        this.reportingService = reportingService;
        this.portfolioService = portfolioService;
    }

    /**
     * Endpoint to fetch all buy trades for a user.
     * Example: GET /api/reporting/{userId}/buy-trades
     */
    @GetMapping("/{userId}/buy-trades")
    public List<TradeResult> getBuyTrades(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return reportingService.getBuyTrades(user);
    }

    /**
     * Endpoint to fetch all sell trades for a user.
     * Example: GET /api/reporting/{userId}/sell-trades
     */
    @GetMapping("/{userId}/sell-trades")
    public List<TradeResult> getSellTrades(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return reportingService.getSellTrades(user);
    }

    /**
     * Endpoint to calculate profit/loss for a user.
     * Example: GET /api/reporting/{userId}/profit-loss
     */
    @GetMapping("/{userId}/profit-loss")
    public double getProfitLoss(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return reportingService.calculateProfitLoss(user);
    }
}
