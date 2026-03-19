package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.domain.LedgerEntry;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.domain.UserPortfolio;
import com.example.tradingsimulationsystem.repository.StockRepository;
import com.example.tradingsimulationsystem.repository.LedgerRepository;
import com.example.tradingsimulationsystem.repository.UserPortfolioRepository;
import com.example.tradingsimulationsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

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
        logger.info("Refreshing user with id={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id={}", userId);
                    return new RuntimeException("User not found: " + userId);
                });
    }

    public void buyStock(User user, String symbol, int quantity) {
        logger.info("Buy request: userId={}, symbol={}, qty={}", user.getId(), symbol, quantity);
        logger.info("Heartbeat check at {}", LocalDateTime.now());

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.warn("Stock not found: {}", symbol);
                    return new RuntimeException("Stock not found: " + symbol);
                });

        if (stock.getAvailableQuantity() < quantity) {
            logger.warn("Not enough stock to buy: symbol={}, available={}, requested={}",
                    symbol, stock.getAvailableQuantity(), quantity);
            throw new RuntimeException("Not enough stock to buy");
        }

        stock.setAvailableQuantity(stock.getAvailableQuantity() - quantity);
        stockRepository.save(stock);
        logger.debug("Stock {} updated, remainingQuantity={}", symbol, stock.getAvailableQuantity());

        UserPortfolio portfolio = userPortfolioRepository.findByUserAndStock(user, stock).orElse(null);
        if (portfolio == null) {
            portfolio = new UserPortfolio(user, stock, quantity);
            logger.debug("New portfolio entry created for userId={}, symbol={}, qty={}", user.getId(), symbol, quantity);
        } else {
            portfolio.addQuantity(quantity);
            logger.debug("Portfolio updated for userId={}, symbol={}, newQty={}", user.getId(), symbol, portfolio.getQuantity());
        }
        userPortfolioRepository.save(portfolio);

        LedgerEntry entry = new LedgerEntry();
        entry.setCompanyName(stock.getDescription());
        entry.setUserId(user.getId());
        entry.setStockSymbol(symbol);
        entry.setQuantity(quantity);
        entry.setPrice(stock.getCurrentPrice());
        entry.setType("BUY");
        entry.setTimestamp(LocalDateTime.now());
        entry.setRemainingQuantity(stock.getAvailableQuantity());
        ledgerRepository.save(entry);

        logger.info("Buy transaction recorded: userId={}, symbol={}, qty={}, price={}", user.getId(), symbol, quantity, stock.getCurrentPrice());
    }

    public void sellStock(User user, String symbol, int quantity) {
        logger.info("Sell request: userId={}, symbol={}, qty={}", user.getId(), symbol, quantity);

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.warn("Stock not found: {}", symbol);
                    return new RuntimeException("Stock not found: " + symbol);
                });

        UserPortfolio portfolio = userPortfolioRepository.findByUserAndStock(user, stock).orElse(null);
        if (portfolio == null || portfolio.getQuantity() < quantity) {
            logger.warn("Not enough holdings to sell: userId={}, symbol={}, available={}, requested={}",
                    user.getId(), symbol, portfolio != null ? portfolio.getQuantity() : 0, quantity);
            throw new RuntimeException("Not enough holdings to sell");
        }

        stock.setAvailableQuantity(stock.getAvailableQuantity() + quantity);
        stockRepository.save(stock);
        logger.debug("Stock {} updated, newAvailableQuantity={}", symbol, stock.getAvailableQuantity());

        portfolio.subtractQuantity(quantity);
        userPortfolioRepository.save(portfolio);
        logger.debug("Portfolio updated for userId={}, symbol={}, newQty={}", user.getId(), symbol, portfolio.getQuantity());

        LedgerEntry entry = new LedgerEntry();
        entry.setUserId(user.getId());
        entry.setStockSymbol(symbol);
        entry.setQuantity(quantity);
        entry.setPrice(stock.getCurrentPrice());
        entry.setType("SELL");
        entry.setTimestamp(LocalDateTime.now());
        entry.setRemainingQuantity(stock.getAvailableQuantity());
        entry.setCompanyName(stock.getDescription());
        ledgerRepository.save(entry);

        logger.info("Sell transaction recorded: userId={}, symbol={}, qty={}, price={}", user.getId(), symbol, quantity, stock.getCurrentPrice());
    }

    public List<UserPortfolio> getUserPortfolio(User user) {
        logger.info("Fetching portfolio for userId={}", user.getId());
        List<UserPortfolio> portfolio = userPortfolioRepository.findByUser(user);
        logger.debug("Portfolio retrieved for userId={}, entries={}", user.getId(), portfolio.size());
        return portfolio;
    }

    public List<LedgerEntry> getUserLedger(Long userId) {
        logger.info("Fetching ledger for userId={}", userId);
        List<LedgerEntry> ledger = ledgerRepository.findByUserId(userId);
        logger.debug("Ledger retrieved for userId={}, entries={}", userId, ledger.size());
        return ledger;
    }

    public double getUserBalance(User user) {
        logger.info("Calculating balance for userId={}", user.getId());
        double balance = userPortfolioRepository.findByUser(user).stream()
                .mapToDouble(p -> p.getQuantity() * p.getStock().getCurrentPrice())
                .sum();
        logger.info("Balance calculated for userId={}, balance={}", user.getId(), balance);
        return balance;
    }

    /**
     * Margin status implementation.
     */
    public String getMarginStatus(User user) {
        logger.info("Checking margin status for userId={}", user.getId());

        double portfolioValue = getUserBalance(user);
        double cashBalance = user.getBalance();
        double marginUsed = user.getMarginUsed();
        double marginAllowedMultiplier = user.getMarginAllowed();

        double equity = cashBalance + portfolioValue - marginUsed;
        double maxMargin = cashBalance * marginAllowedMultiplier;

        String status;
        if (equity < 0) {
            status = "Margin call: account equity is negative!";
            logger.warn("Margin call triggered for userId={}, equity={}", user.getId(), equity);
        } else if (marginUsed > maxMargin) {
            status = String.format("Margin call: margin used (%.2f) exceeds allowed (%.2f).", marginUsed, maxMargin);
            logger.warn("Margin call triggered for userId={}, marginUsed={}, maxAllowed={}", user.getId(), marginUsed, maxMargin);
        } else {
            double marginRatio = equity / (portfolioValue + cashBalance) * 100;
            status = String.format("Margin healthy: Equity = %.2f, Margin Used = %.2f, Allowed = %.2f (Ratio %.1f%%)",
                    equity, marginUsed, maxMargin, marginRatio);
            logger.info("Margin healthy for userId={}, equity={}, marginUsed={}, maxAllowed={}, ratio={}",
                    user.getId(), equity, marginUsed, maxMargin, marginRatio);
        }
        return status;
    }
}
