package com.example.tradingsimulationsystem.dto;
//import com.example.tradingsimulationsystem.controller.StockController;



public class StockDTO {
    private String symbol;
    private String displaySymbol;
    private String description;

    // Quote fields
    private double currentPrice;   // c
    private double change;         // d
    private double percentChange;  // dp
    private double high;           // h
    private double low;            // l
    private double open;           // o
    private double previousClose;  // pc

    public StockDTO(String symbol, String displaySymbol, String description,
                    double currentPrice, double change, double percentChange,
                    double high, double low, double open, double previousClose) {
        this.symbol = symbol;
        this.displaySymbol = displaySymbol;
        this.description = description;
        this.currentPrice = currentPrice;
        this.change = change;
        this.percentChange = percentChange;
        this.high = high;
        this.low = low;
        this.open = open;
        this.previousClose = previousClose;
    }

    // --- Getters & Setters ---
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }

    public double getPercentChange() { return percentChange; }
    public void setPercentChange(double percentChange) { this.percentChange = percentChange; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }

    public double getPreviousClose() { return previousClose; }
    public void setPreviousClose(double previousClose) { this.previousClose = previousClose; }
}
