package com.example.tradingsimulationsystem.dto;

// Profile response from Finnhub /stock/profile2
public class ProfileResponse {
    private String name;
    private double shareOutstanding;

    // Getters & setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getShareOutstanding() { return shareOutstanding; }
    public void setShareOutstanding(double shareOutstanding) { this.shareOutstanding = shareOutstanding; }
}
