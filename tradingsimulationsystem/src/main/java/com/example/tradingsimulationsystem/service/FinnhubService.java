package com.example.tradingsimulationsystem.service;

import com.example.tradingsimulationsystem.dto.FinnhubSymbol;
import com.example.tradingsimulationsystem.dto.StockQuote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Service
public class FinnhubService {

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

    public List<FinnhubSymbol> getSymbols(String exchange) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/stock/symbol")
                        .queryParam("exchange", exchange)
                        .queryParam("token", finnhubApiKey)
                        .build())
                .retrieve()
                .bodyToFlux(FinnhubSymbol.class)
                .collectList()
                .block();
    }

    public StockQuote getQuote(String symbol) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", finnhubApiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(StockQuote.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Error fetching quote for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
}