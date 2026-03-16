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
    private double balance = 10000.0;    // Default starting balance

    @Column(nullable = false)
    private double marginAllowed = 5.0; // Default margin (5x)

    @Column(nullable = false)
    private double marginUsed = 0.0;    // Track how much margin is currently used

    @Column(nullable = false)
    private String role = "USER";       // Default role for new users

    // Portfolio holdings: one user can hold many stocks
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPortfolio> portfolios = new HashSet<>();

    // --- Constructors ---
    public User() {}

    public User(String username, String password, double balance, double marginAllowed, String role) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.marginAllowed = marginAllowed;
        this.role = role;
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

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<UserPortfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(Set<UserPortfolio> portfolios) { this.portfolios = portfolios; }
}
