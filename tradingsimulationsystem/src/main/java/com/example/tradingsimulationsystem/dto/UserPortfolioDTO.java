package com.example.tradingsimulationsystem.dto;

public class UserPortfolioDTO {

    private String stockSymbol;
    private int quantity;
    private double currentPrice;
    private double totalValue;

    public UserPortfolioDTO() {}

    public UserPortfolioDTO(String stockSymbol, int quantity, double currentPrice) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
        this.totalValue = quantity * currentPrice;
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
        this.totalValue = this.quantity * this.currentPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        this.totalValue = this.quantity * this.currentPrice;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
