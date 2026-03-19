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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report")
public class ReportingController {

    private static final Logger logger = LoggerFactory.getLogger(ReportingController.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.trading.simulation.audit");

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

    @GetMapping("/trades/{userId}")
    public List<TradeResultDTO> getTradeHistory(
            @PathVariable Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        logger.info("Fetching trade history for userId={}, from={}, to={}", userId, from, to);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        List<TradeResult> trades = tradeResultRepository.findByBuyerOrSeller(user, user);

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

        logger.info("Retrieved {} trades for userId={}", trades.size(), userId);
        auditLogger.info("User {} viewed trade history", userId);

        return trades.stream()
                .map(TradeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/pnl/{userId}")
    public double getProfitLoss(@PathVariable Long userId) {
        logger.info("Fetching PnL for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        List<UserPortfolio> portfolios = portfolioService.getUserPortfolio(user);

        double portfolioValue = portfolios.stream()
                .mapToDouble(p -> p.getQuantity() * p.getStock().getCurrentPrice())
                .sum();

        double totalBalance = user.getBalance();
        double marginUsed = user.getMarginUsed();

        double pnl = portfolioValue + totalBalance - marginUsed;
        logger.info("PnL for userId={} calculated as {}", userId, pnl);
        auditLogger.info("User {} viewed PnL summary", userId);

        return pnl;
    }

    @GetMapping("/portfolio/{userId}")
    public List<PortfolioDTO> getPortfolioSummary(@PathVariable Long userId) {
        logger.info("Fetching portfolio summary for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        List<UserPortfolio> portfolios = portfolioService.getUserPortfolio(user);
        auditLogger.info("User {} viewed portfolio summary", userId);

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
