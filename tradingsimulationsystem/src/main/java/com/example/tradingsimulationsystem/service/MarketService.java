package com.example.tradingsimulationsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class MarketService {

    private final LocalTime marketOpen;
    private final LocalTime marketClose;

    public MarketService(
            @Value("${market.open:09:00}") String openTime,
            @Value("${market.close:15:30}") String closeTime) {
        this.marketOpen = LocalTime.parse(openTime);
        this.marketClose = LocalTime.parse(closeTime);
    }

    /**
     * Check if the market is currently open.
     */
    public boolean isMarketOpen() {
        LocalTime now = LocalTime.now();
        return !now.isBefore(marketOpen) && !now.isAfter(marketClose);
    }

    /**
     * Get a human-readable market status message.
     */
    public String getMarketStatusMessage() {
        LocalTime now = LocalTime.now();
        if (isMarketOpen()) {
            return "Market is OPEN (closes at " + marketClose + ")";
        } else if (now.isBefore(marketOpen)) {
            return "Market is CLOSED (opens at " + marketOpen + ")";
        } else {
            return "Market is CLOSED (next open tomorrow at " + marketOpen + ")";
        }
    }
}
