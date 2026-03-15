package com.example.tradingsimulationsystem.dto;

import com.example.tradingsimulationsystem.domain.TradeType;

public class TradeRequestDTO {

    private Long userId;
    private Long stockId;
    private int quantity;
    private TradeType tradeType; // BUY or SELL

    public TradeRequestDTO() {}

    public TradeRequestDTO(Long userId, Long stockId, int quantity, TradeType tradeType) {
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.tradeType = tradeType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }
}
