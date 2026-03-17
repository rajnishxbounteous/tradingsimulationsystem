package com.example.tradingsimulationsystem.dto;

public class SymbolResponse {
    private String symbol;          // e.g. "AAPL"
    private String displaySymbol;   // e.g. "AAPL"
    private String description;     // e.g. "Apple Inc"
    private String currency;        // e.g. "USD"
    private String type;            // e.g. "Common Stock"
    private String mic;             // Market Identifier Code, e.g. "XNAS"

    // --- Getters and Setters ---
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

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getMic() {
        return mic;
    }
    public void setMic(String mic) {
        this.mic = mic;
    }
}
