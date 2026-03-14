package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.*;
import com.example.tradingsimulationsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Submit a trade request for concurrent execution.
     */
    public void processTradeRequest(TradeRequest request) {
        executorService.submit(() -> executeTrade(request));
    }

    /**
     * Execute trade logic: validate market, balance/margin, update portfolios, record result.
     */
    @Transactional
    public void executeTrade(TradeRequest request) {
        if (!marketService.isMarketOpen()) {
            System.out.println("Trade rejected: Market is closed.");
            return;
        }

        User user = request.getUser();
        Stock stock = request.getStock();
        int quantity = request.getQuantity();
        double price = stock.getPrice();

        if (request.getTradeType() == TradeType.BUY) {
            double totalCost = price * quantity;

            // Check balance + margin
            if (user.getBalance() + (user.getMarginAllowed() - user.getMarginUsed()) < totalCost) {
                System.out.println("Trade rejected: Insufficient funds or margin.");
                return;
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
            UserPortfolio portfolio = portfolioRepository.findByUserAndStock(user, stock);
            if (portfolio == null) {
                portfolio = new UserPortfolio(user, stock, quantity);
            } else {
                portfolio.setQuantity(portfolio.getQuantity() + quantity);
            }
            portfolioRepository.save(portfolio);

            // Record trade result (buyer only, seller is "market")
            TradeResult result = new TradeResult(user, null, stock, quantity, price);
            tradeResultRepository.save(result);

        } else if (request.getTradeType() == TradeType.SELL) {
            UserPortfolio portfolio = portfolioRepository.findByUserAndStock(user, stock);
            if (portfolio == null || portfolio.getQuantity() < quantity) {
                System.out.println("Trade rejected: Not enough shares to sell.");
                return;
            }

            // Deduct shares
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            portfolioRepository.save(portfolio);

            // Credit balance
            double proceeds = price * quantity;
            user.setBalance(user.getBalance() + proceeds);

            // Record trade result (seller only, buyer is "market")
            TradeResult result = new TradeResult(null, user, stock, quantity, price);
            tradeResultRepository.save(result);
        }

        userRepository.save(user);
        stockRepository.save(stock);
    }
}
