package com.example.tradingsimulationsystem.dto;

/**
 * Data Transfer Object for returning trade results via API.
 * Carries only the essential info for frontend display.
 */
public class TradeResultDTO {

    private String stockSymbol;   // Symbol of the traded stock
    private int quantity;         // Number of shares traded
    private double executedPrice; // Price at which trade executed
    private String status;        // SUCCESS or FAILED message
    private String timestamp;     // Execution time as string

    public TradeResultDTO() {}

    public TradeResultDTO(String stockSymbol, int quantity, double executedPrice, String status, String timestamp) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.executedPrice = executedPrice;
        this.status = status;
        this.timestamp = timestamp;
    }

    // --- Getters & Setters ---
    public String getStockSymbol() { return stockSymbol; }
    public void setStockSymbol(String stockSymbol) { this.stockSymbol = stockSymbol; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getExecutedPrice() { return executedPrice; }
    public void setExecutedPrice(double executedPrice) { this.executedPrice = executedPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
