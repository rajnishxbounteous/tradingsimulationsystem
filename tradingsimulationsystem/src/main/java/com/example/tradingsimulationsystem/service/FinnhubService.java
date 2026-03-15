package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.dto.FinnhubSymbol;
import com.example.tradingsimulationsystem.dto.StockQuote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class FinnhubService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public FinnhubService() {
        this.webClient = WebClient.create("https://finnhub.io/api/v1");
    }

    public StockQuote getQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(StockQuote.class)
                .block();
    }

    /**
     * Fetch list of US stock symbols from Finnhub.
     * You can later filter or limit to 50 for free tier.
     */
    public List<FinnhubSymbol> getSymbols(String exchange) {
        FinnhubSymbol[] symbols = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stock/symbol")
                        .queryParam("exchange", exchange)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubSymbol[].class)
                .block();

        return Arrays.asList(symbols);
    }
}
