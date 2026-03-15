package com.example.tradingsimulationsystem.dto;

/**
 * DTO representing Finnhub's /quote API response.
 * Fields map directly to JSON keys returned by Finnhub.
 */
public class StockQuote {
    private double c;  // Current price
    private double o;  // Open price
    private double h;  // High price
    private double l;  // Low price
    private double pc; // Previous close

    // Getters and setters
    public double getC() { return c; }
    public void setC(double c) { this.c = c; }

    public double getO() { return o; }
    public void setO(double o) { this.o = o; }

    public double getH() { return h; }
    public void setH(double h) { this.h = h; }

    public double getL() { return l; }
    public void setL(double l) { this.l = l; }

    public double getPc() { return pc; }
    public void setPc(double pc) { this.pc = pc; }
}
