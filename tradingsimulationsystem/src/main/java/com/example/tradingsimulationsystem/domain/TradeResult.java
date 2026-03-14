package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_results")
public class TradeResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Buyer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    // Seller
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // Stock traded
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private int quantity;   // Number of shares traded

    @Column(nullable = false)
    private double executedPrice; // Price at which trade executed

    @Column(nullable = false)
    private LocalDateTime timestamp; // When trade executed

    // --- Constructors ---
    public TradeResult() {}

    public TradeResult(User buyer, User seller, Stock stock, int quantity, double executedPrice) {
        this.buyer = buyer;
        this.seller = seller;
        this.stock = stock;
        this.quantity = quantity;
        this.executedPrice = executedPrice;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getExecutedPrice() { return executedPrice; }
    public void setExecutedPrice(double executedPrice) { this.executedPrice = executedPrice; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
