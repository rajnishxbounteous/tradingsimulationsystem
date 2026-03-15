package com.example.tradingsimulationsystem.dto;

public class FinnhubSymbol {
    private String symbol;
    private String displaySymbol;
    private String description;

    // Getters and setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
