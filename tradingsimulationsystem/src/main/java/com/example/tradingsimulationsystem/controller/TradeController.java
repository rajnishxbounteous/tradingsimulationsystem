package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.TradeRequest;
import com.example.tradingsimulationsystem.domain.TradeResult;
import com.example.tradingsimulationsystem.domain.User;
import com.example.tradingsimulationsystem.dto.TradeRequestDTO;
import com.example.tradingsimulationsystem.dto.TradeResultDTO;
import com.example.tradingsimulationsystem.mapper.TradeMapper;
import com.example.tradingsimulationsystem.repository.StockRepository;
import com.example.tradingsimulationsystem.repository.TradeResultRepository;
import com.example.tradingsimulationsystem.repository.UserRepository;
import com.example.tradingsimulationsystem.service.MarketService;
import com.example.tradingsimulationsystem.service.TradeProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.trading.simulation.audit");

    private final TradeProcessorService tradeProcessorService;
    private final MarketService marketService;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final TradeResultRepository tradeResultRepository;

    public TradeController(TradeProcessorService tradeProcessorService,
                           MarketService marketService,
                           StockRepository stockRepository,
                           UserRepository userRepository,
                           TradeResultRepository tradeResultRepository) {
        this.tradeProcessorService = tradeProcessorService;
        this.marketService = marketService;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
        this.tradeResultRepository = tradeResultRepository;
    }

    /**
     * Endpoint to submit a trade request (BUY/SELL).
     * Example: POST /api/trades
     */
    @PostMapping
    public TradeResultDTO submitTrade(@RequestBody TradeRequestDTO dto) {
        logger.info("Trade request submitted: userId={}, symbol={}, type={}, qty={}",
                dto.getUserId(), dto.getSymbol(), dto.getTradeType(), dto.getQuantity());

        // 1. Validate market status
        if (!marketService.isMarketOpen()) {
            logger.warn("Trade rejected: Market closed for userId={}", dto.getUserId());
            throw new IllegalStateException("Market is closed. Trades cannot be processed.");
        }

        // 2. Validate symbol exists in DB
        if (!stockRepository.existsBySymbol(dto.getSymbol())) {
            logger.warn("Trade rejected: Invalid symbol {} for userId={}", dto.getSymbol(), dto.getUserId());
            throw new IllegalArgumentException("Invalid stock symbol: " + dto.getSymbol());
        }

        // 3. Fetch user
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    logger.warn("Trade rejected: User not found {}", dto.getUserId());
                    return new IllegalArgumentException("User not found: " + dto.getUserId());
                });

        // 4. Map DTO → entity
        TradeRequest request = TradeMapper.toEntity(dto, user);

        // 5. Delegate to trade processor
        TradeResult result = tradeProcessorService.processTradeRequest(request);

        // 6. Log success
        logger.info("Trade executed successfully: userId={}, symbol={}, type={}, qty={}, price={}",
                dto.getUserId(), dto.getSymbol(), dto.getTradeType(), dto.getQuantity(), result.getExecutedPrice());
        auditLogger.info("User {} executed {} {} shares of {} at price {}",
                dto.getUserId(), dto.getTradeType(), dto.getQuantity(), dto.getSymbol(), result.getExecutedPrice());

        // 7. Map entity → DTO for response
        return TradeMapper.toDTO(result);
    }

    /**
     * Endpoint to fetch all trades for a given user.
     * Example: GET /api/trades/{userId}
     */
    @GetMapping("/{userId}")
    public List<TradeResultDTO> getTradeHistory(@PathVariable Long userId) {
        logger.info("Fetching trade history for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Trade history request failed: User not found {}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        List<TradeResult> trades = tradeResultRepository.findByBuyerOrSeller(user, user);
        logger.info("Retrieved {} trades for userId={}", trades.size(), userId);
        auditLogger.info("User {} viewed trade history", userId);

        return trades.stream()
                .map(TradeMapper::toDTO)
                .collect(Collectors.toList());
    }
}
