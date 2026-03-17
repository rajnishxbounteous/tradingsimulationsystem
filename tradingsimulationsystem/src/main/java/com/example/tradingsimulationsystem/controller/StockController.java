package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<StockDTO> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .map(stock -> new StockDTO(
                        stock.getSymbol(),
                        stock.getDisplaySymbol(),
                        stock.getDescription(),
                        stock.getCurrentPrice(),   // c
                        stock.getChange(),         // d
                        stock.getPercentChange(),  // dp
                        stock.getHigh(),           // h
                        stock.getLow(),            // l
                        stock.getOpen(),           // o
                        stock.getPreviousClose()   // pc
                ))
                .collect(Collectors.toList());
    }

}
