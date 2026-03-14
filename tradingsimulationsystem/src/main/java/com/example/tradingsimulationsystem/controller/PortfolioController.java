package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Endpoint to fetch all portfolio holdings for a user.
     * Example: GET /api/portfolio/{userId}
     */
    @GetMapping("/{userId}")
    public List<UserPortfolio> getUserPortfolio(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return portfolioService.getUserPortfolio(user);
    }

    /**
     * Endpoint to fetch current balance of a user.
     * Example: GET /api/portfolio/{userId}/balance
     */
    @GetMapping("/{userId}/balance")
    public double getUserBalance(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return portfolioService.getUserBalance(user);
    }

    /**
     * Endpoint to fetch margin status of a user.
     * Example: GET /api/portfolio/{userId}/margin
     */
    @GetMapping("/{userId}/margin")
    public String getMarginStatus(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return portfolioService.getMarginStatus(user);
    }
}
