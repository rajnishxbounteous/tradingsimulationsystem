package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.*;
import com.example.tradingsimulationsystem.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class TradeProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(TradeProcessorService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

    private final MarketService marketService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserPortfolioRepository portfolioRepository;
    private final TradeResultRepository tradeResultRepository;
    private final LedgerRepository ledgerRepository;

    // Thread pool for concurrent trade execution
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TradeProcessorService(MarketService marketService,
                                 UserRepository userRepository,
                                 StockRepository stockRepository,
                                 UserPortfolioRepository portfolioRepository,
                                 TradeResultRepository tradeResultRepository,
                                 LedgerRepository ledgerRepository) {
        this.marketService = marketService;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.portfolioRepository = portfolioRepository;
        this.tradeResultRepository = tradeResultRepository;
        this.ledgerRepository = ledgerRepository;
    }

    /**
     * Submit a trade request for concurrent execution and return the result.
     */
    public TradeResult processTradeRequest(TradeRequest request) {
        logger.info("Processing trade request: userId={}, symbol={}, qty={}, type={}",
                request.getUser().getId(), request.getSymbol(), request.getQuantity(), request.getTradeType());

        try {
            Future<TradeResult> future = executorService.submit(() -> executeTrade(request));
            TradeResult result = future.get(); // block until trade finishes
            logger.info("Trade request completed: {}", result.getStatus());
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Trade execution interrupted for userId={}, symbol={}", request.getUser().getId(), request.getSymbol(), e);
            throw new RuntimeException("Trade execution interrupted", e);
        } catch (ExecutionException e) {
            logger.error("Trade execution failed for userId={}, symbol={}", request.getUser().getId(), request.getSymbol(), e);
            throw new RuntimeException("Trade execution failed", e);
        }
    }

    /**
     * Execute trade logic: validate market, balance/margin, update portfolios, record result.
     * Runs inside worker thread with its own transaction.
     */
    @Transactional
    public TradeResult executeTrade(TradeRequest request) {
        logger.info("Executing trade: userId={}, symbol={}, qty={}, type={}",
                request.getUser().getId(), request.getSymbol(), request.getQuantity(), request.getTradeType());

        if (!marketService.isMarketOpen()) {
            logger.warn("Trade failed: Market closed");
            return new TradeResult(null, null, null, 0, 0.0, "FAILED: Market closed");
        }

        Stock stock = stockRepository.findBySymbol(request.getSymbol()).orElse(null);
        if (stock == null) {
            logger.warn("Trade failed: Invalid symbol {}", request.getSymbol());
            return new TradeResult(null, null, null, 0, 0.0, "FAILED: Invalid symbol");
        }

        User user = userRepository.findById(request.getUser().getId()).orElse(null);
        if (user == null) {
            logger.warn("Trade failed: User not found id={}", request.getUser().getId());
            return new TradeResult(null, null, stock, 0, 0.0, "FAILED: User not found");
        }

        int quantity = request.getQuantity();
        double price = stock.getCurrentPrice();

        if (request.getTradeType() == TradeType.BUY) {
            double totalCost = price * quantity;
            logger.debug("BUY trade: userId={}, symbol={}, qty={}, totalCost={}", user.getId(), stock.getSymbol(), quantity, totalCost);

            if (user.getBalance() + (user.getMarginAllowed() - user.getMarginUsed()) < totalCost) {
                logger.warn("Trade failed: Insufficient funds for userId={}, required={}, available={}",
                        user.getId(), totalCost, user.getBalance());
                return new TradeResult(user, null, stock, quantity, price, "FAILED: Insufficient funds");
            }

            if (user.getBalance() >= totalCost) {
                user.setBalance(user.getBalance() - totalCost);
            } else {
                double remaining = totalCost - user.getBalance();
                user.setBalance(0);
                user.setMarginUsed(user.getMarginUsed() + remaining);
            }

            Optional<UserPortfolio> portfolioOpt = portfolioRepository.findByUserAndStock(user, stock);
            UserPortfolio portfolio = portfolioOpt.orElseGet(() -> new UserPortfolio(user, stock, 0));
            portfolio.setQuantity(portfolio.getQuantity() + quantity);
            portfolioRepository.save(portfolio);

            TradeResult result = new TradeResult(user, null, stock, quantity, price, "SUCCESS: BUY executed");
            tradeResultRepository.save(result);

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
            logger.info("BUY trade executed successfully: userId={}, symbol={}, qty={}, price={}", user.getId(), stock.getSymbol(), quantity, price);
            return result;

        } else if (request.getTradeType() == TradeType.SELL) {
            logger.debug("SELL trade: userId={}, symbol={}, qty={}", user.getId(), stock.getSymbol(), quantity);

            Optional<UserPortfolio> portfolioOpt = portfolioRepository.findByUserAndStock(user, stock);
            if (portfolioOpt.isEmpty() || portfolioOpt.get().getQuantity() < quantity) {
                logger.warn("Trade failed: Not enough shares to sell for userId={}, symbol={}, requested={}, available={}",
                        user.getId(), stock.getSymbol(), quantity, portfolioOpt.map(UserPortfolio::getQuantity).orElse(0));
                return new TradeResult(null, user, stock, quantity, price, "FAILED: Not enough shares");
            }

            UserPortfolio portfolio = portfolioOpt.get();
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            portfolioRepository.save(portfolio);

            double proceeds = price * quantity;
            user.setBalance(user.getBalance() + proceeds);

            TradeResult result = new TradeResult(null, user, stock, quantity, price, "SUCCESS: SELL executed");
            tradeResultRepository.save(result);

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
            logger.info("SELL trade executed successfully: userId={}, symbol={}, qty={}, price={}", user.getId(), stock.getSymbol(), quantity, price);
            return result;
        }

        logger.error("Trade failed: Unknown trade type {}", request.getTradeType());
        return new TradeResult(user, null, stock, quantity, price, "FAILED: Unknown trade type");
    }
}
