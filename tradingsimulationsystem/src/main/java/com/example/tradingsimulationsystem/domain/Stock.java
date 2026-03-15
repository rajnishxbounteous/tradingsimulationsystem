package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;          // e.g., AAPL

    @Column(nullable = false)
    private double price;           // current price

    private String displaySymbol;   // e.g., AAPL (NASDAQ)
    private String description;     // e.g., Apple Inc.

    // Constructors
    public Stock() {}

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public Stock(String symbol, double price, String displaySymbol, String description) {
        this.symbol = symbol;
        this.price = price;
        this.displaySymbol = displaySymbol;
        this.description = description;
    }

    // Getters and setters
    public Long getId() { return id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
