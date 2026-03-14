package com.example.tradingsimulationsystem.domain;

public enum OrderType {
    MARKET,   // Executes immediately at current price
    LIMIT     // Executes only if price condition is met
}
