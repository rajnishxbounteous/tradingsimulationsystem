package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.domain.LedgerEntry;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.repository.StockRepository;
import com.example.tradingsimulationsystem.repository.LedgerRepository;
import com.example.tradingsimulationsystem.repository.UserPortfolioRepository;
import com.example.tradingsimulationsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PortfolioService {

    private final StockRepository stockRepository;
    private final LedgerRepository ledgerRepository;
    private final UserPortfolioRepository userPortfolioRepository;
    private final UserRepository userRepository;

    public PortfolioService(StockRepository stockRepository,
                            LedgerRepository ledgerRepository,
                            UserPortfolioRepository userPortfolioRepository,
                            UserRepository userRepository) {
        this.stockRepository = stockRepository;
        this.ledgerRepository = ledgerRepository;
        this.userPortfolioRepository = userPortfolioRepository;
        this.userRepository = userRepository;
    }

    public User refreshUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    public void buyStock(User user, String symbol, int quantity) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Not enough stock to buy");
        }

        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stockRepository.save(stock);

        UserPortfolio portfolio = userPortfolioRepository.findByUserAndStock(user, stock)
                .orElse(null);

        if (portfolio == null) {
            portfolio = new UserPortfolio(user, stock, quantity);
        } else {
            portfolio.addQuantity(quantity);
        }
        userPortfolioRepository.save(portfolio);

        LedgerEntry entry = new LedgerEntry();
        entry.setUserId(user.getId());
        entry.setStockSymbol(symbol);
        entry.setQuantity(quantity);
        entry.setPrice(stock.getCurrentPrice());
        entry.setType("BUY");
        entry.setTimestamp(LocalDateTime.now());
        entry.setRemainingQuantity(stock.getAvailableQuantity());
        ledgerRepository.save(entry);
    }

    public void sellStock(User user, String symbol, int quantity) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        UserPortfolio portfolio = userPortfolioRepository.findByUserAndStock(user, stock)
                .orElse(null);

        if (portfolio == null || portfolio.getQuantity() < quantity) {
            throw new RuntimeException("Not enough holdings to sell");
        }

        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        stockRepository.save(stock);

        portfolio.subtractQuantity(quantity);
        userPortfolioRepository.save(portfolio);

        LedgerEntry entry = new LedgerEntry();
        entry.setUserId(user.getId());
        entry.setStockSymbol(symbol);
        entry.setQuantity(quantity);
        entry.setPrice(stock.getCurrentPrice());
        entry.setType("SELL");
        entry.setTimestamp(LocalDateTime.now());
        entry.setRemainingQuantity(stock.getAvailableQuantity());
        ledgerRepository.save(entry);
    }

    public List<UserPortfolio> getUserPortfolio(User user) {
        return userPortfolioRepository.findByUser(user);
    }

    public List<LedgerEntry> getUserLedger(Long userId) {
        return ledgerRepository.findByUserId(userId);
    }

    public double getUserBalance(User user) {
        return userPortfolioRepository.findByUser(user).stream()
                .mapToDouble(p -> p.getQuantity() * p.getStock().getCurrentPrice())
                .sum();
    }

    /**
     * Margin status implementation.
     */
    public String getMarginStatus(User user) {
        double portfolioValue = getUserBalance(user);
        double cashBalance = user.getBalance();
        double marginUsed = user.getMarginUsed();
        double marginAllowedMultiplier = user.getMarginAllowed();

        // Equity = cash + portfolio - margin used
        double equity = cashBalance + portfolioValue - marginUsed;

        // Maximum margin buying power
        double maxMargin = cashBalance * marginAllowedMultiplier;

        if (equity < 0) {
            return "Margin call: account equity is negative!";
        } else if (marginUsed > maxMargin) {
            return String.format("Margin call: margin used (%.2f) exceeds allowed (%.2f).",
                    marginUsed, maxMargin);
        } else {
            double marginRatio = equity / (portfolioValue + cashBalance) * 100;
            return String.format("Margin healthy: Equity = %.2f, Margin Used = %.2f, Allowed = %.2f (Ratio %.1f%%)",
                    equity, marginUsed, maxMargin, marginRatio);
        }
    }
}
