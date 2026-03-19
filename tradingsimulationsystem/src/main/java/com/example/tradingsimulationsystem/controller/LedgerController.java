package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.LedgerEntry;
import com.example.tradingsimulationsystem.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private static final Logger logger = LoggerFactory.getLogger(LedgerController.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.trading.simulation.audit");

    private final PortfolioService portfolioService;

    public LedgerController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Get full trade history (ledger) for a user.
     */
    @GetMapping("/{userId}")
    public List<LedgerEntry> getUserLedger(@PathVariable Long userId) {
        logger.info("Fetching ledger for userId={}", userId);
        List<LedgerEntry> ledger = portfolioService.getUserLedger(userId);
        logger.info("Ledger retrieved successfully for userId={}", userId);
        auditLogger.info("User {} viewed full trade history", userId);
        return ledger;
    }
}
