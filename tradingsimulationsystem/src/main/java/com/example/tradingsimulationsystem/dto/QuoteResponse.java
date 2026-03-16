package com.example.tradingsimulationsystem.dto;

// Quote response from Finnhub /quote
public class QuoteResponse {
    private double c; // current price
    private double h; // high
    private double l; // low
    private double o; // open
    private double pc; // previous close

    // Getters & setters
    public double getC() { return c; }
    public void setC(double c) { this.c = c; }

    public double getH() { return h; }
    public void setH(double h) { this.h = h; }

    public double getL() { return l; }
    public void setL(double l) { this.l = l; }

    public double getO() { return o; }
    public void setO(double o) { this.o = o; }

    public double getPc() { return pc; }
    public void setPc(double pc) { this.pc = pc; }
}
