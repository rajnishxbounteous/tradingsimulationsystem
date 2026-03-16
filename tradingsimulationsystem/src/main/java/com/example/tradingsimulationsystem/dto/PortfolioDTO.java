package com.example.tradingsimulationsystem.dto;

/**
 * Data Transfer Object for exposing portfolio holdings via API.
 * Represents a single stock holding in a user's portfolio.
 */
public class PortfolioDTO {

    private String symbol;
    private String displaySymbol;
    private String description;
    private int quantity;
    private double currentPrice;
    private double totalValue;

    // --- Constructor ---
    public PortfolioDTO(String symbol,
                        String displaySymbol,
                        String description,
                        int quantity,
                        double currentPrice,
                        double totalValue) {
        this.symbol = symbol;
        this.displaySymbol = displaySymbol;
        this.description = description;
        this.quantity = quantity;
        this.currentPrice = currentPrice;
        this.totalValue = totalValue;
    }

    // --- Getters & Setters ---
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDisplaySymbol() {
        return displaySymbol;
    }
    public void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getTotalValue() {
        return totalValue;
    }
    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
}
