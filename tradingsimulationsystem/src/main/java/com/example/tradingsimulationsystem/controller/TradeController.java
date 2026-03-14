package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.TradeRequest;
import com.example.tradingsimulationsystem.service.TradeProcessorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeProcessorService tradeProcessorService;

    public TradeController(TradeProcessorService tradeProcessorService) {
        this.tradeProcessorService = tradeProcessorService;
    }

    /**
     * Endpoint to submit a trade request (BUY/SELL).
     * Example: POST /api/trades
     */
    @PostMapping
    public String submitTrade(@RequestBody TradeRequest request) {
        tradeProcessorService.processTradeRequest(request);
        return "Trade request submitted for processing.";
    }
}
