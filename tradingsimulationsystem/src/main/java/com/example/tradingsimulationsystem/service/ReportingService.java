package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.repository.TradeResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingService {

    private final TradeResultRepository tradeResultRepository;

    public ReportingService(TradeResultRepository tradeResultRepository) {
        this.tradeResultRepository = tradeResultRepository;
    }

    /**
     * Get all trades executed where the user was the buyer.
     */
    public List<TradeResult> getBuyTrades(User user) {
        return tradeResultRepository.findByBuyer(user);
    }

    /**
     * Get all trades executed where the user was the seller.
     */
    public List<TradeResult> getSellTrades(User user) {
        return tradeResultRepository.findBySeller(user);
    }

    /**
     * Calculate profit/loss summary for a user.
     * (Simple version: proceeds from sales - cost of purchases)
     */
    public double calculateProfitLoss(User user) {
        double buyTotal = getBuyTrades(user).stream()
                .mapToDouble(trade -> trade.getExecutedPrice() * trade.getQuantity())
                .sum();

        double sellTotal = getSellTrades(user).stream()
                .mapToDouble(trade -> trade.getExecutedPrice() * trade.getQuantity())
                .sum();

        return sellTotal - buyTotal;
    }
}
