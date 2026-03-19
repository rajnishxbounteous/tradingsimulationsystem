package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.repository.TradeResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingService {

    private static final Logger logger = LoggerFactory.getLogger(ReportingService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

    private final TradeResultRepository tradeResultRepository;

    public ReportingService(TradeResultRepository tradeResultRepository) {
        this.tradeResultRepository = tradeResultRepository;
    }

    /**
     * Get all trades executed where the user was the buyer.
     */
    public List<TradeResult> getBuyTrades(User user) {
        logger.info("Fetching buy trades for userId={}", user.getId());
        List<TradeResult> buys = tradeResultRepository.findByBuyer(user);
        logger.debug("Retrieved {} buy trades for userId={}", buys.size(), user.getId());
        return buys;
    }

    /**
     * Get all trades executed where the user was the seller.
     */
    public List<TradeResult> getSellTrades(User user) {
        logger.info("Fetching sell trades for userId={}", user.getId());
        List<TradeResult> sells = tradeResultRepository.findBySeller(user);
        logger.debug("Retrieved {} sell trades for userId={}", sells.size(), user.getId());
        return sells;
    }

    /**
     * Calculate profit/loss summary for a user.
     * (Simple version: proceeds from sales - cost of purchases)
     */
    public double calculateProfitLoss(User user) {
        logger.info("Calculating profit/loss for userId={}", user.getId());

        double buyTotal = getBuyTrades(user).stream()
                .mapToDouble(trade -> trade.getExecutedPrice() * trade.getQuantity())
                .sum();

        double sellTotal = getSellTrades(user).stream()
                .mapToDouble(trade -> trade.getExecutedPrice() * trade.getQuantity())
                .sum();

        double profitLoss = sellTotal - buyTotal;
        if (profitLoss >= 0) {
            logger.info("Profit calculated for userId={}, profit={}", user.getId(), profitLoss);
        } else {
            logger.warn("Loss calculated for userId={}, loss={}", user.getId(), profitLoss);
        }
        return profitLoss;
    }
}
