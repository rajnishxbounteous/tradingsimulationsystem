package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_requests")
public class TradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the user placing the order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Link to the stock being traded
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;   // BUY or SELL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;   // MARKET or LIMIT

    @Column(nullable = false)
    private int quantity;          // Number of shares

    @Column(nullable = true)
    private Double limitPrice;     // Price for limit orders (null for market orders)

    @Column(nullable = false)
    private LocalDateTime timestamp; // When the request was placed

    // --- Constructors ---
    public TradeRequest() {}

    public TradeRequest(User user, Stock stock, TradeType tradeType,
                        OrderType orderType, int quantity, Double limitPrice) {
        this.user = user;
        this.stock = stock;
        this.tradeType = tradeType;
        this.orderType = orderType;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.timestamp = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }

    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) { this.orderType = orderType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Double getLimitPrice() { return limitPrice; }
    public void setLimitPrice(Double limitPrice) { this.limitPrice = limitPrice; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
