package com.example.tradingsimulationsystem.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class MarketService {

    // Define market open and close times
    private final LocalTime marketOpen = LocalTime.of(9, 0);   // 9:00 AM
    private final LocalTime marketClose = LocalTime.of(15, 30); // 3:30 PM

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
        return isMarketOpen() ? "Market is OPEN" : "Market is CLOSED";
    }
}
