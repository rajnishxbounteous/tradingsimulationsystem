package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.dto.PortfolioDTO;
import com.example.tradingsimulationsystem.dto.BuyRequest;
import com.example.tradingsimulationsystem.dto.SellRequest;
import com.example.tradingsimulationsystem.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.trading.simulation.audit");

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{userId}")
    public List<PortfolioDTO> getUserPortfolio(@PathVariable Long userId) {
        logger.info("Fetching portfolio for userId={}", userId);
        User user = portfolioService.refreshUser(userId);
        List<UserPortfolio> portfolios = portfolioService.getUserPortfolio(user);
        auditLogger.info("User {} viewed portfolio holdings", userId);

        return portfolios.stream()
                .map(p -> new PortfolioDTO(
                        p.getStock().getSymbol(),
                        p.getStock().getDisplaySymbol(),
                        p.getStock().getDescription(),
                        p.getQuantity(),
                        p.getStock().getCurrentPrice(),
                        p.getQuantity() * p.getStock().getCurrentPrice()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}/balance")
    public double getUserBalance(@PathVariable Long userId) {
        logger.info("Fetching balance for userId={}", userId);
        User user = portfolioService.refreshUser(userId);
        double balance = portfolioService.getUserBalance(user);
        auditLogger.info("User {} viewed balance: {}", userId, balance);
        return balance;
    }

    @GetMapping("/{userId}/margin")
    public String getMarginStatus(@PathVariable Long userId) {
        logger.info("Fetching margin status for userId={}", userId);
        User user = portfolioService.refreshUser(userId);
        String marginStatus = portfolioService.getMarginStatus(user);
        auditLogger.info("User {} viewed margin status: {}", userId, marginStatus);
        return marginStatus;
    }

    @PostMapping("/{userId}/buy")
    public ResponseEntity<?> buyStock(@PathVariable Long userId,
                                      @RequestBody BuyRequest buyRequest) {
        logger.info("Buy request: userId={}, symbol={}, qty={}", userId, buyRequest.getSymbol(), buyRequest.getQuantity());
        try {
            User user = portfolioService.refreshUser(userId);
            portfolioService.buyStock(user, buyRequest.getSymbol(), buyRequest.getQuantity());
            logger.info("Buy successful for userId={}, symbol={}, qty={}", userId, buyRequest.getSymbol(), buyRequest.getQuantity());
            auditLogger.info("User {} bought {} shares of {}", userId, buyRequest.getQuantity(), buyRequest.getSymbol());
            return ResponseEntity.ok("Stock purchased successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Buy failed for userId={} - {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during buy for userId={}", userId, e);
            return ResponseEntity.internalServerError().body("Purchase failed: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/sell")
    public ResponseEntity<?> sellStock(@PathVariable Long userId,
                                       @RequestBody SellRequest sellRequest) {
        logger.info("Sell request: userId={}, symbol={}, qty={}", userId, sellRequest.getSymbol(), sellRequest.getQuantity());
        try {
            User user = portfolioService.refreshUser(userId);
            portfolioService.sellStock(user, sellRequest.getSymbol(), sellRequest.getQuantity());
            logger.info("Sell successful for userId={}, symbol={}, qty={}", userId, sellRequest.getSymbol(), sellRequest.getQuantity());
            auditLogger.info("User {} sold {} shares of {}", userId, sellRequest.getQuantity(), sellRequest.getSymbol());
            return ResponseEntity.ok("Stock sold successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("Sell failed for userId={} - {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during sell for userId={}", userId, e);
            return ResponseEntity.internalServerError().body("Sale failed: " + e.getMessage());
        }
    }
}
