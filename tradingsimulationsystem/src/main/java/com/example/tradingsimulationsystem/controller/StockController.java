package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller to expose available stocks and their current prices.
 * Frontend can call /api/stocks to get the list of tradable symbols.
 */
@RestController
public class StockController {

    private final StockRepository stockRepository;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Get all stocks currently available in the system.
     * Returns symbol, description, displaySymbol, and current price.
     */
    @GetMapping("/api/stocks")
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
}
