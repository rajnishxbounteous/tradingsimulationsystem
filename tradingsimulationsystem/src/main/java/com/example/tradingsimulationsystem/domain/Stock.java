package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    private String displaySymbol;
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "name")
    private String name;

    // --- Quote fields ---
    @Column(name = "price")   // <-- FIXED: matches DB column
    private double currentPrice;   // c

    @Column(name = "change")
    private double change;         // d

    @Column(name = "percent_change")
    private double percentChange;  // dp

    @Column(name = "high")
    private double high;           // h

    @Column(name = "low")
    private double low;            // l

    @Column(name = "open")
    private double open;           // o

    @Column(name = "previous_close")
    private double previousClose;  // pc

    // constructors, getters, setters...

    // --- Constructors ---
    public Stock() {}

//    public Stock(String symbol, double currentPrice, int availableQuantity) {
//        this.symbol = symbol;
//        this.currentPrice = currentPrice;
//        this.availableQuantity = availableQuantity;
//    }

    public Stock(String symbol, double currentPrice, String displaySymbol, String description,
                 int availableQuantity, String name,
                 double change, double percentChange, double high, double low,
                 double open, double previousClose) {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.displaySymbol = displaySymbol;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.name = name;
        this.change = change;
        this.percentChange = percentChange;
        this.high = high;
        this.low = low;
        this.open = open;
        this.previousClose = previousClose;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
