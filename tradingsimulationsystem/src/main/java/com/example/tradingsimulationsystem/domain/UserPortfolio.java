package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;

/**
 * Entity representing a user's holding of a particular stock.
 * Each record links a user to a stock and tracks the quantity owned.
 */
@Entity
@Table(name = "user_portfolios")
public class UserPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Link to the stock being held
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private int quantity;   // Number of shares owned

    // --- Constructors ---
    public UserPortfolio() {
        // Default constructor required by JPA
    }

    public UserPortfolio(User user, Stock stock, int quantity) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }
    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
