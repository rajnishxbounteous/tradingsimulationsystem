package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;   // e.g., TCS, INFY

    @Column(nullable = false)
    private String name;     // Full name of the company

    @Column(nullable = false)
    private double price;    // Current market price

    @Column(nullable = false)
    private int availableQuantity; // Shares available in market

    // --- Constructors ---
    public Stock() {}

    public Stock(String symbol, String name, double price, int availableQuantity) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
}
