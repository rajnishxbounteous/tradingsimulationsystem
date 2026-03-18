package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.dto.PortfolioDTO;
import com.example.tradingsimulationsystem.dto.BuyRequest;
import com.example.tradingsimulationsystem.dto.SellRequest;
import com.example.tradingsimulationsystem.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Fetch all portfolio holdings for a user.
     * Example: GET /api/portfolio/{userId}
     */
    @GetMapping("/{userId}")
    public List<PortfolioDTO> getUserPortfolio(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        List<UserPortfolio> portfolios = portfolioService.getUserPortfolio(user);

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

    /**
     * Fetch current balance of a user.
     * Example: GET /api/portfolio/{userId}/balance
     */
    @GetMapping("/{userId}/balance")
    public double getUserBalance(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return portfolioService.getUserBalance(user);
    }

    /**
     * Fetch margin status of a user.
     * Example: GET /api/portfolio/{userId}/margin
     */
    @GetMapping("/{userId}/margin")
    public String getMarginStatus(@PathVariable Long userId) {
        User user = portfolioService.refreshUser(userId);
        return portfolioService.getMarginStatus(user);
    }

    /**
     * Buy stocks for a user.
     * Example: POST /api/portfolio/{userId}/buy
     * Body: { "symbol": "AAPL", "quantity": 10 }
     */
    @PostMapping("/{userId}/buy")
    public ResponseEntity<?> buyStock(@PathVariable Long userId,
                                      @RequestBody BuyRequest buyRequest) {
        try {
            User user = portfolioService.refreshUser(userId);
            portfolioService.buyStock(user, buyRequest.getSymbol(), buyRequest.getQuantity());
            return ResponseEntity.ok("Stock purchased successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Purchase failed: " + e.getMessage());
        }
    }

    /**
     * Sell stocks for a user.
     * Example: POST /api/portfolio/{userId}/sell
     * Body: { "symbol": "AAPL", "quantity": 5 }
     */
    @PostMapping("/{userId}/sell")
    public ResponseEntity<?> sellStock(@PathVariable Long userId,
                                       @RequestBody SellRequest sellRequest) {
        try {
            User user = portfolioService.refreshUser(userId);
            portfolioService.sellStock(user, sellRequest.getSymbol(), sellRequest.getQuantity());
            return ResponseEntity.ok("Stock sold successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Sale failed: " + e.getMessage());
        }
    }


}
