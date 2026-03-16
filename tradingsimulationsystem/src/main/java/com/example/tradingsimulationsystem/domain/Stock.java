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

    @Column(nullable = false)
    private double price;

    private String displaySymbol;
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "name")
    private String name;

    // --- Constructors ---
    public Stock() {}

    public Stock(String symbol, double price, int availableQuantity) {
        this.symbol = symbol;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public Stock(String symbol, double price, String displaySymbol, String description, int availableQuantity, String name) {
        this.symbol = symbol;
        this.price = price;
        this.displaySymbol = displaySymbol;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.name = name;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
