package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockQuote;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for periodically updating stock prices
 * from Finnhub and saving them into the database.
 */
@Service
public class MarketUpdaterService {

    private final StockRepository stockRepository;
    private final FinnhubService finnhubService;

    public MarketUpdaterService(StockRepository stockRepository, FinnhubService finnhubService) {
        this.stockRepository = stockRepository;
        this.finnhubService = finnhubService;
    }

    /**
     * Refresh stock prices every 60 seconds using Finnhub API.
     * This ensures the market data stays live and realistic.
     */
    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void updateMarketPrices() {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            try {
                StockQuote quote = finnhubService.getQuote(stock.getSymbol());
                if (quote != null && quote.getC() > 0) {
                    stock.setPrice(quote.getC());
                    stockRepository.save(stock);
                }
            } catch (Exception e) {
                System.err.println("Failed to update price for " + stock.getSymbol() + ": " + e.getMessage());
            }
        }
    }
}
