package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PriceController {

    private final StockRepository stockRepository;

    public PriceController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Endpoint to fetch all current stock prices.
     * Example: GET /api/stocks
     */
    @GetMapping("/api/stocks")
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
}
