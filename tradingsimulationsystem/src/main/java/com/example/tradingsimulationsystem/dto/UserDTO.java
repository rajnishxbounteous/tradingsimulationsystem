package com.example.tradingsimulationsystem.dto;

public class UserDTO {

    private Long id;
    private String username;
    private double balance;
    private double marginAllowed;
    private double marginUsed;

    public UserDTO() {}

    public UserDTO(Long id, String username, double balance, double marginAllowed, double marginUsed) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.marginAllowed = marginAllowed;
        this.marginUsed = marginUsed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMarginAllowed() {
        return marginAllowed;
    }

    public void setMarginAllowed(double marginAllowed) {
        this.marginAllowed = marginAllowed;
    }

    public double getMarginUsed() {
        return marginUsed;
    }

    public void setMarginUsed(double marginUsed) {
        this.marginUsed = marginUsed;
    }
}
