package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.service.FinnhubService; // Assuming you have this service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finnhub")
public class FinnhubController {

    @Autowired
    private FinnhubService finnhubService; // Inject your existing Finnhub service

    @GetMapping("/stock/candle")
    public Map<String, Object> getStockCandle(
            @RequestParam String symbol,
            @RequestParam String resolution, // e.g., "D" for daily
            @RequestParam long from, // Unix timestamp
            @RequestParam long to   // Unix timestamp
    ) {
        // Call Finnhub API for candle data
        return finnhubService.getStockCandle(symbol, resolution, from, to);
    }
}