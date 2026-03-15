package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.*;
import com.example.tradingsimulationsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TradeProcessorService {

    private final MarketService marketService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserPortfolioRepository portfolioRepository;
    private final TradeResultRepository tradeResultRepository;

    // Thread pool for concurrent trade execution
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TradeProcessorService(MarketService marketService,
                                 UserRepository userRepository,
                                 StockRepository stockRepository,
                                 UserPortfolioRepository portfolioRepository,
                                 TradeResultRepository tradeResultRepository) {
        this.marketService = marketService;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.portfolioRepository = portfolioRepository;
        this.tradeResultRepository = tradeResultRepository;
    }

    /**
     * Submit a trade request for concurrent execution and return the result.
     */
    public TradeResult processTradeRequest(TradeRequest request) {
        // Run trade synchronously for now (can be async if needed)
        return executeTrade(request);
    }

    /**
     * Execute trade logic: validate market, balance/margin, update portfolios, record result.
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
        double price = stock.getPrice();

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

            userRepository.save(user);
            return result;
        }

        return new TradeResult(user, null, stock, quantity, price, "FAILED: Unknown trade type");
    }
}
