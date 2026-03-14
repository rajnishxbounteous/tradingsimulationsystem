package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PriceSimulationService {

    private final StockRepository stockRepository;
    private final Random random = new Random();

    public PriceSimulationService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Scheduled task to update stock prices every 5 seconds.
     * Simulates market fluctuations.
     */
    @Scheduled(fixedRate = 5000)
    public void updateStockPrices() {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            double currentPrice = stock.getPrice();

            // Simulate random price change between -5% and +5%
            double changePercent = (random.nextDouble() * 10) - 5;
            double newPrice = currentPrice + (currentPrice * changePercent / 100);

            // Ensure price doesn’t go below 1
            if (newPrice < 1) newPrice = 1;

            stock.setPrice(newPrice);
            stockRepository.save(stock);
        }
    }
}
