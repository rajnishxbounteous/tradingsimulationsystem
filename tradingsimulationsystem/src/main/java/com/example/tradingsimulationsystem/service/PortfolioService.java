package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.repository.UserPortfolioRepository;
import com.example.tradingsimulationsystem.repository.UserRepository;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final UserRepository userRepository;
    private final UserPortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;

    public PortfolioService(UserRepository userRepository,
                            UserPortfolioRepository portfolioRepository,
                            StockRepository stockRepository) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.stockRepository = stockRepository;
    }

    /**
     * Refresh user from DB (ensures latest state).
     */
    public User refreshUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    /**
     * Get all portfolio holdings for a given user.
     */
    public List<UserPortfolio> getUserPortfolio(User user) {
        return portfolioRepository.findByUser(user);
    }

    /**
     * Get the current cash balance of a user.
     */
    public double getUserBalance(User user) {
        return user.getBalance();
    }

    /**
     * Get margin usage details for a user.
     */
    public String getMarginStatus(User user) {
        return "Margin Allowed: " + user.getMarginAllowed() +
                ", Margin Used: " + user.getMarginUsed();
    }

    /**
     * Buy stocks for a user.
     */
    public void buyStock(Long userId, String symbol, int quantity) {
        User user = refreshUser(userId);
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + symbol));

        double totalCost = stock.getPrice() * quantity;

        if (user.getBalance() < totalCost) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        if (stock.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Deduct balance and update stock availability
        user.setBalance(user.getBalance() - totalCost);
        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);

        // Update portfolio
        UserPortfolio portfolio = portfolioRepository.findByUserAndStock(user, stock)
                .orElse(new UserPortfolio(user, stock, 0));
        portfolio.setQuantity(portfolio.getQuantity() + quantity);

        // Save changes
        userRepository.save(user);
        stockRepository.save(stock);
        portfolioRepository.save(portfolio);
    }

    /**
     * Sell stocks for a user.
     */
    public void sellStock(Long userId, String symbol, int quantity) {
        User user = refreshUser(userId);
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + symbol));

        UserPortfolio portfolio = portfolioRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new IllegalArgumentException("User does not own this stock"));

        if (portfolio.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock quantity to sell");
        }

        double totalProceeds = stock.getPrice() * quantity;

        // Increase balance and stock availability
        user.setBalance(user.getBalance() + totalProceeds);
        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);

        // Update portfolio
        portfolio.setQuantity(portfolio.getQuantity() - quantity);

        // Save changes
        userRepository.save(user);
        stockRepository.save(stock);
        portfolioRepository.save(portfolio);
    }
}
