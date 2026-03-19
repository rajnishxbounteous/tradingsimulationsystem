package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.domain.Stock;
import com.example.tradingsimulationsystem.dto.StockQuote;
import com.example.tradingsimulationsystem.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for periodically updating stock prices
 * from Finnhub and saving them into the database.
 */
@Service
public class MarketUpdaterService {

    private static final Logger logger = LoggerFactory.getLogger(MarketUpdaterService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

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
        logger.info("Starting scheduled market price update");

        List<Stock> stocks = stockRepository.findAll();
        logger.debug("Found {} stocks to update", stocks.size());

        for (Stock stock : stocks) {
            try {
                logger.debug("Fetching quote for symbol={}", stock.getSymbol());
                StockQuote quote = finnhubService.getQuote(stock.getSymbol());

                if (quote != null && quote.getC() > 0) {
                    double oldPrice = stock.getCurrentPrice();
                    stock.setCurrentPrice(quote.getC()); // update with current price
                    stockRepository.save(stock);
                    logger.info("Updated price for symbol={} from {} to {}", stock.getSymbol(), oldPrice, quote.getC());
                } else {
                    logger.warn("No valid quote received for symbol={}", stock.getSymbol());
                }
            } catch (Exception e) {
                logger.error("Failed to update price for symbol={}", stock.getSymbol(), e);
            }
        }

        logger.info("Market price update cycle completed");
    }
}
