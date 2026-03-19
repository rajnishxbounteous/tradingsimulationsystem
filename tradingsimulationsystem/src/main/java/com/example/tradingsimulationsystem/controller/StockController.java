package com.example.tradingsimulationsystem.controller;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockDTO;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    private final StockRepository stockRepository;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @GetMapping("/api/stocks")
    public List<StockDTO> getAllStocks() {
        logger.info("Fetching all available stocks");
        List<Stock> stocks = stockRepository.findAll();
        logger.info("Retrieved {} stocks", stocks.size());

        return stocks.stream()
                .map(stock -> new StockDTO(
                        stock.getSymbol(),
                        stock.getDisplaySymbol(),
                        stock.getDescription(),
                        stock.getCurrentPrice(),
                        stock.getChange(),
                        stock.getPercentChange(),
                        stock.getHigh(),
                        stock.getLow(),
                        stock.getOpen(),
                        stock.getPreviousClose()
                ))
                .collect(Collectors.toList());
    }
}
