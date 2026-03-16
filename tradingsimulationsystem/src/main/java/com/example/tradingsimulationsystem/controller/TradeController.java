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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

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
        // 1. Validate market status
        if (!marketService.isMarketOpen()) {
            throw new IllegalStateException("Market is closed. Trades cannot be processed.");
        }

        // 2. Validate symbol exists in DB
        if (!stockRepository.existsBySymbol(dto.getSymbol())) {
            throw new IllegalArgumentException("Invalid stock symbol: " + dto.getSymbol());
        }

        // 3. Fetch user
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));

        // 4. Map DTO → entity
        TradeRequest request = TradeMapper.toEntity(dto, user);

        // 5. Delegate to trade processor
        TradeResult result = tradeProcessorService.processTradeRequest(request);

        // 6. Map entity → DTO for response
        return TradeMapper.toDTO(result);
    }

    /**
     * Endpoint to fetch all trades for a given user.
     * Example: GET /api/trades/{userId}
     */
    @GetMapping("/{userId}")
    public List<TradeResultDTO> getTradeHistory(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<TradeResult> trades = tradeResultRepository.findByBuyerOrSeller(user, user);

        return trades.stream()
                .map(TradeMapper::toDTO)
                .collect(Collectors.toList());
    }
}
