package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PriceSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(PriceSimulationService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

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
        logger.info("Starting scheduled price simulation update");

        List<Stock> stocks = stockRepository.findAll();
        logger.debug("Found {} stocks to simulate", stocks.size());

        for (Stock stock : stocks) {
            try {
                double currentPrice = stock.getCurrentPrice();

                // Simulate random price change between -5% and +5%
                double changePercent = (random.nextDouble() * 10) - 5;
                double newPrice = currentPrice + (currentPrice * changePercent / 100);

                // Ensure price doesn’t go below 1
                if (newPrice < 1) {
                    logger.warn("Simulated price for symbol={} dropped below 1, resetting to 1", stock.getSymbol());
                    newPrice = 1;
                }

                stock.setCurrentPrice(newPrice);
                stockRepository.save(stock);

                logger.info("Simulated price update: symbol={}, oldPrice={}, newPrice={}, changePercent={}",
                        stock.getSymbol(), currentPrice, newPrice, changePercent);

            } catch (Exception e) {
                logger.error("Failed to simulate price for symbol={}", stock.getSymbol(), e);
            }
        }

        logger.info("Price simulation update cycle completed");
    }
}
