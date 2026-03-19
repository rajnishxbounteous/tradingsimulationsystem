package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.dto.FinnhubSymbol;
import com.example.tradingsimulationsystem.dto.StockQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;
import java.util.Map;

@Service
public class FinnhubService {

    private static final Logger logger = LoggerFactory.getLogger(FinnhubService.class);
    private static final Logger auditLogger = LoggerFactory.getLogger("com.example.tradingsimulationsystem.audit");

    private final WebClient webClient;
    private final String finnhubApiKey;

    public FinnhubService(WebClient.Builder webClientBuilder,
                          @Value("${finnhub.api.key}") String finnhubApiKey,
                          @Value("${finnhub.base.url}") String baseUrl) {

        // CRITICAL: Configure HttpClient to follow redirects (handles the 302 error)
        HttpClient httpClient = HttpClient.create().followRedirect(true);

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .build();
        this.finnhubApiKey = finnhubApiKey;
    }

    /**
     * Fetch all symbols for a given exchange.
     */
    public List<FinnhubSymbol> getSymbols(String exchange) {
        logger.info("Fetching symbols for exchange={}", exchange);
        try {
            List<FinnhubSymbol> symbols = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/stock/symbol")
                            .queryParam("exchange", exchange)
                            .queryParam("token", finnhubApiKey)
                            .build())
                    .retrieve()
                    .bodyToFlux(FinnhubSymbol.class)
                    .collectList()
                    .block();

            logger.info("Retrieved {} symbols for exchange={}", symbols != null ? symbols.size() : 0, exchange);
            return symbols;
        } catch (Exception e) {
            logger.error("Error fetching symbols for exchange={}", exchange, e);
            return null;
        }
    }

    /**
     * Fetch real-time stock quote for a symbol.
     */
    public StockQuote getQuote(String symbol) {
        logger.info("Fetching quote for symbol={}", symbol);
        try {
            StockQuote quote = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", finnhubApiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(StockQuote.class)
                    .block();

            if (quote != null) {
                logger.info("Quote fetched successfully for symbol={}, currentPrice={}", symbol, quote.getC());
            } else {
                logger.warn("Quote response was null for symbol={}", symbol);
            }
            return quote;
        } catch (Exception e) {
            logger.error("Error fetching quote for symbol={}", symbol, e);
            return null;
        }
    }

    /**
     * Fetch historical candle data for a symbol.
     */
    public Map<String, Object> getStockCandle(String symbol, String resolution, long from, long to) {
        logger.info("Fetching candle data for symbol={}, resolution={}, from={}, to={}", symbol, resolution, from, to);
        try {
            Map<String, Object> candleData = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/stock/candle")
                            .queryParam("symbol", symbol)
                            .queryParam("resolution", resolution)
                            .queryParam("from", from)
                            .queryParam("to", to)
                            .queryParam("token", finnhubApiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (candleData != null) {
                logger.info("Candle data fetched successfully for symbol={}, resolution={}", symbol, resolution);
            } else {
                logger.warn("Candle data response was null for symbol={}", symbol);
            }
            return candleData;
        } catch (Exception e) {
            logger.error("Error fetching candle data for symbol={}", symbol, e);
            return null;
        }
    }
}
