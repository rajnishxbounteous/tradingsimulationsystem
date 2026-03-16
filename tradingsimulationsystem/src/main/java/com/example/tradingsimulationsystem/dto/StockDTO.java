package com.example.tradingsimulationsystem.dto;

public class StockDTO {
    private String symbol;
    private String displaySymbol;
    private String description;
    private double price;

    public StockDTO(String symbol, String displaySymbol, String description, double price) {
        this.symbol = symbol;
        this.displaySymbol = displaySymbol;
        this.description = description;
        this.price = price;
    }

    // --- Getters & Setters ---
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
