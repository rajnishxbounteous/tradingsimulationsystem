package com.example.tradingsimulationsystem.dto;

public class TradeResultDTO {

    private String stockSymbol;
    private int quantity;
    private double executedPrice;
    private String tradeType; // BUY or SELL
    private String executedAt; // timestamp as string

    public TradeResultDTO() {}

    public TradeResultDTO(String stockSymbol, int quantity, double executedPrice, String tradeType, String executedAt) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.executedPrice = executedPrice;
        this.tradeType = tradeType;
        this.executedAt = executedAt;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(double executedPrice) {
        this.executedPrice = executedPrice;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(String executedAt) {
        this.executedAt = executedAt;
    }
}
