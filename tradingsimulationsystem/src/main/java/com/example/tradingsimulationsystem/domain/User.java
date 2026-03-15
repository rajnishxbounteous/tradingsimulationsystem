package com.example.tradingsimulationsystem.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;   // e.g., Rajnish

    @Column(nullable = false)
    private String password;   // Encrypted password (BCrypt)

    @Column(nullable = false)
    private double balance;    // Available cash balance

    @Column(nullable = false)
    private double marginAllowed; // e.g., 5x margin

    @Column(nullable = false)
    private double marginUsed = 0.0; // Track how much margin is currently used

    // Portfolio holdings: one user can hold many stocks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPortfolio> portfolios = new HashSet<>();

    // --- Constructors ---
    public User() {}

    public User(String username, String password, double balance, double marginAllowed) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.marginAllowed = marginAllowed;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getMarginAllowed() { return marginAllowed; }
    public void setMarginAllowed(double marginAllowed) { this.marginAllowed = marginAllowed; }

    public double getMarginUsed() { return marginUsed; }
    public void setMarginUsed(double marginUsed) { this.marginUsed = marginUsed; }

    public Set<UserPortfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(Set<UserPortfolio> portfolios) { this.portfolios = portfolios; }
}
