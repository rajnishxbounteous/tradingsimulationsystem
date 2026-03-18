package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.*;
import com.example.tradingsimulationsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class TradeProcessorService {

    private final MarketService marketService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserPortfolioRepository portfolioRepository;
    private final TradeResultRepository tradeResultRepository;
    private final LedgerRepository ledgerRepository; // NEW

    // Thread pool for concurrent trade execution
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TradeProcessorService(MarketService marketService,
                                 UserRepository userRepository,
                                 StockRepository stockRepository,
                                 UserPortfolioRepository portfolioRepository,
                                 TradeResultRepository tradeResultRepository,
                                 LedgerRepository ledgerRepository) { // NEW
        this.marketService = marketService;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.portfolioRepository = portfolioRepository;
        this.tradeResultRepository = tradeResultRepository;
        this.ledgerRepository = ledgerRepository; // NEW
    }

    /**
     * Submit a trade request for concurrent execution and return the result.
     */
    public TradeResult processTradeRequest(TradeRequest request) {
        try {
            // Submit to thread pool and wait for result
            Future<TradeResult> future = executorService.submit(() -> executeTrade(request));
            return future.get(); // block until trade finishes
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Trade execution interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Trade execution failed", e);
        }
    }

    /**
     * Execute trade logic: validate market, balance/margin, update portfolios, record result.
     * Runs inside worker thread with its own transaction.
     */
    @Transactional
    public TradeResult executeTrade(TradeRequest request) {
        if (!marketService.isMarketOpen()) {
            return new TradeResult(null, null, null, 0, 0.0, "FAILED: Market closed");
        }

        // Fetch fresh stock from DB
        Stock stock = stockRepository.findBySymbol(request.getSymbol())
                .orElse(null);
        if (stock == null) {
            return new TradeResult(null, null, null, 0, 0.0, "FAILED: Invalid symbol");
        }

        User user = userRepository.findById(request.getUser().getId())
                .orElse(null);
        if (user == null) {
            return new TradeResult(null, null, stock, 0, 0.0, "FAILED: User not found");
        }

        int quantity = request.getQuantity();
        double price = stock.getCurrentPrice();

        if (request.getTradeType() == TradeType.BUY) {
            double totalCost = price * quantity;

            // Check balance + margin
            if (user.getBalance() + (user.getMarginAllowed() - user.getMarginUsed()) < totalCost) {
                return new TradeResult(user, null, stock, quantity, price, "FAILED: Insufficient funds");
            }

            // Deduct balance or margin
            if (user.getBalance() >= totalCost) {
                user.setBalance(user.getBalance() - totalCost);
            } else {
                double remaining = totalCost - user.getBalance();
                user.setBalance(0);
                user.setMarginUsed(user.getMarginUsed() + remaining);
            }

            // Update portfolio
            Optional<UserPortfolio> portfolioOpt = portfolioRepository.findByUserAndStock(user, stock);
            UserPortfolio portfolio = portfolioOpt.orElseGet(() -> new UserPortfolio(user, stock, 0));
            portfolio.setQuantity(portfolio.getQuantity() + quantity);
            portfolioRepository.save(portfolio);

            // Record trade result
            TradeResult result = new TradeResult(user, null, stock, quantity, price, "SUCCESS: BUY executed");
            tradeResultRepository.save(result);

            // NEW: Save ledger entry
            LedgerEntry ledgerEntry = new LedgerEntry();
            ledgerEntry.setUserId(user.getId());
            ledgerEntry.setStockSymbol(stock.getSymbol());
            ledgerEntry.setType(TradeType.BUY.name());
            ledgerEntry.setQuantity(quantity);
            ledgerEntry.setPrice(price);
            ledgerEntry.setTimestamp(LocalDateTime.now());
            ledgerEntry.setCompanyName(stock.getDescription());
            ledgerEntry.setRemainingQuantity(portfolio.getQuantity());
            ledgerRepository.save(ledgerEntry);

            userRepository.save(user);
            return result;

        } else if (request.getTradeType() == TradeType.SELL) {
            Optional<UserPortfolio> portfolioOpt = portfolioRepository.findByUserAndStock(user, stock);
            if (portfolioOpt.isEmpty() || portfolioOpt.get().getQuantity() < quantity) {
                return new TradeResult(null, user, stock, quantity, price, "FAILED: Not enough shares");
            }

            UserPortfolio portfolio = portfolioOpt.get();
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            portfolioRepository.save(portfolio);

            // Credit balance
            double proceeds = price * quantity;
            user.setBalance(user.getBalance() + proceeds);

            // Record trade result
            TradeResult result = new TradeResult(null, user, stock, quantity, price, "SUCCESS: SELL executed");
            tradeResultRepository.save(result);

            // NEW: Save ledger entry
            LedgerEntry ledgerEntry = new LedgerEntry();
            ledgerEntry.setUserId(user.getId());
            ledgerEntry.setStockSymbol(stock.getSymbol());
            ledgerEntry.setType(TradeType.SELL.name());
            ledgerEntry.setQuantity(quantity);
            ledgerEntry.setPrice(price);
            ledgerEntry.setTimestamp(LocalDateTime.now());
            ledgerEntry.setCompanyName(stock.getDescription());
            ledgerEntry.setRemainingQuantity(portfolio.getQuantity());
            ledgerRepository.save(ledgerEntry);

            userRepository.save(user);
            return result;
        }

        return new TradeResult(user, null, stock, quantity, price, "FAILED: Unknown trade type");
    }
}
