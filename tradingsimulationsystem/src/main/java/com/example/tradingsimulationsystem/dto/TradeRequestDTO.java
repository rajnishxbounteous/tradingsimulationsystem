package com.example.tradingsimulationsystem.dto;

import com.example.tradingsimulationsystem.domain.TradeType;

/**
 * Data Transfer Object for submitting trade requests via API.
 * Carries only the minimal info needed from the client.
 */
public class TradeRequestDTO {

    private Long userId;          // ID of the user submitting the trade
    private String symbol;        // Stock symbol (e.g., "AAPL")
    private int quantity;         // Number of shares
    private TradeType tradeType;  // BUY or SELL

    public TradeRequestDTO() {}

    public TradeRequestDTO(Long userId, String symbol, int quantity, TradeType tradeType) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.tradeType = tradeType;
    }

    // --- Getters & Setters ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }
}
