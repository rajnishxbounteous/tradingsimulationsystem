package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;

/**
 * Represents a trade request submitted by a user.
 * This is the input object for trade processing.
 */
@Entity
@Table(name = "trade_requests")
public class TradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User submitting the trade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Symbol of the stock being traded
    @Column(nullable = false)
    private String symbol;

    // Trade type: BUY or SELL
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;

    // Number of shares
    @Column(nullable = false)
    private int quantity;

    // --- Constructors ---
    public TradeRequest() {}

    public TradeRequest(User user, String symbol, TradeType tradeType, int quantity) {
        this.user = user;
        this.symbol = symbol;
        this.tradeType = tradeType;
        this.quantity = quantity;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
