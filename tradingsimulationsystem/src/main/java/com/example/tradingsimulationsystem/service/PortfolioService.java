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

    /**
     * Utility method to fetch a User entity by ID.
     */
    public User refreshUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    /**
     * Buy stock for a user.
     */
    public void buyStock(User user, String symbol, int quantity) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        if (stock.getAvailableQuantity() < quantity) {
            throw new RuntimeException("Not enough stock to buy");
        }

        // Update stock availability
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stockRepository.save(stock);

        // Update user portfolio
        UserPortfolio portfolio = userPortfolioRepository.findByUserAndStock(user, stock)
                .orElse(null);

        if (portfolio == null) {
            portfolio = new UserPortfolio(user, stock, quantity);
        } else {
            portfolio.addQuantity(quantity);
        }
        userPortfolioRepository.save(portfolio);

        // Record in ledger
        LedgerEntry entry = new LedgerEntry();
        entry.setUserId(user.getId());
        entry.setStockSymbol(symbol);
        entry.setQuantity(quantity);
        entry.setPrice(stock.getPrice());
        entry.setType("BUY");
        entry.setTimestamp(LocalDateTime.now());
        entry.setRemainingQuantity(stock.getAvailableQuantity());
        ledgerRepository.save(entry);
    }

    /**
     * Sell stock for a user.
     */
    public void sellStock(User user, String symbol, int quantity) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        UserPortfolio portfolio = userPortfolioRepository.findByUserAndStock(user, stock)
                .orElse(null);

        if (portfolio == null || portfolio.getQuantity() < quantity) {
            throw new RuntimeException("Not enough holdings to sell");
        }

        // Update stock availability
        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        stockRepository.save(stock);

        // Update user portfolio
        portfolio.subtractQuantity(quantity);
        userPortfolioRepository.save(portfolio);

        // Record in ledger
        LedgerEntry entry = new LedgerEntry();
        entry.setUserId(user.getId());
        entry.setStockSymbol(symbol);
        entry.setQuantity(quantity);
        entry.setPrice(stock.getPrice());
        entry.setType("SELL");
        entry.setTimestamp(LocalDateTime.now());
        entry.setRemainingQuantity(stock.getAvailableQuantity());
        ledgerRepository.save(entry);
    }

    /**
     * Fetch all portfolio holdings for a user.
     */
    public List<UserPortfolio> getUserPortfolio(User user) {
        return userPortfolioRepository.findByUser(user);
    }

    /**
     * Fetch all ledger entries for a user.
     */
    public List<LedgerEntry> getUserLedger(Long userId) {
        return ledgerRepository.findByUserId(userId);
    }

    /**
     * Example: calculate user balance (sum of holdings).
     */
    public double getUserBalance(User user) {
        return userPortfolioRepository.findByUser(user).stream()
                .mapToDouble(p -> p.getQuantity() * p.getStock().getPrice())
                .sum();
    }

    /**
     * Example: margin status placeholder.
     */
    public String getMarginStatus(User user) {
        // Implement your own margin logic here
        return "Margin status not implemented";
    }
}
