package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.dto.PortfolioDTO;
import com.example.tradingsimulationsystem.dto.TradeResultDTO;
import com.example.tradingsimulationsystem.mapper.TradeMapper;
import com.example.tradingsimulationsystem.repository.TradeResultRepository;
import com.example.tradingsimulationsystem.repository.UserRepository;
import com.example.tradingsimulationsystem.service.PortfolioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report")
public class ReportingController {

    private final TradeResultRepository tradeResultRepository;
    private final UserRepository userRepository;
    private final PortfolioService portfolioService;

    public ReportingController(TradeResultRepository tradeResultRepository,
                               UserRepository userRepository,
                               PortfolioService portfolioService) {
        this.tradeResultRepository = tradeResultRepository;
        this.userRepository = userRepository;
        this.portfolioService = portfolioService;
    }

    /**
     * Get all trades executed by a user, optionally filtered by date range.
     * Example: GET /api/report/trades/{userId}?from=2026-03-01T00:00:00&to=2026-03-15T23:59:59
     */
    @GetMapping("/trades/{userId}")
    public List<TradeResultDTO> getTradeHistory(
            @PathVariable Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<TradeResult> trades = tradeResultRepository.findByBuyerOrSeller(user, user);

        // Apply date range filter if provided
        if (from != null) {
            trades = trades.stream()
                    .filter(t -> !t.getTimestamp().isBefore(from))
                    .collect(Collectors.toList());
        }
        if (to != null) {
            trades = trades.stream()
                    .filter(t -> !t.getTimestamp().isAfter(to))
                    .collect(Collectors.toList());
        }

        return trades.stream()
                .map(TradeMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get profit/loss summary for a user.
     * Example: GET /api/report/pnl/{userId}
     */
    @GetMapping("/pnl/{userId}")
    public double getProfitLoss(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<UserPortfolio> portfolios = portfolioService.getUserPortfolio(user);

        double portfolioValue = portfolios.stream()
                .mapToDouble(p -> p.getQuantity() * p.getStock().getCurrentPrice())
                .sum();

        double totalBalance = user.getBalance();
        double marginUsed = user.getMarginUsed();

        return portfolioValue + totalBalance - marginUsed;
    }

    /**
     * Get portfolio performance summary for a user.
     * Example: GET /api/report/portfolio/{userId}
     */
    @GetMapping("/portfolio/{userId}")
    public List<PortfolioDTO> getPortfolioSummary(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

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
}
