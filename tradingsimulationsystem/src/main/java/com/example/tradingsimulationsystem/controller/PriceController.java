package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final StockRepository stockRepository;

    public PriceController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Get all current stock prices.
     * Example: GET /api/prices
     */
    @GetMapping
    public List<StockDTO> getAllPrices() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .map(s -> new StockDTO(
                        s.getSymbol(),
                        s.getDisplaySymbol(),
                        s.getDescription(),
                        s.getPrice()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get current price for a specific symbol.
     * Example: GET /api/prices/AAPL
     */
    @GetMapping("/{symbol}")
    public StockDTO getPriceBySymbol(@PathVariable String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock symbol: " + symbol));

        return new StockDTO(
                stock.getSymbol(),
                stock.getDisplaySymbol(),
                stock.getDescription(),
                stock.getPrice()
        );
    }
}
