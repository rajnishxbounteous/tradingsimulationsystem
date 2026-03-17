package com.example.tradingsimulationsystem.dto;

// Quote response from Finnhub /quote
public class QuoteResponse {
    private double c;   // current price
    private double d;   // change (absolute value)
    private double dp;  // percent change
    private double h;   // high price of the day
    private double l;   // low price of the day
    private double o;   // open price of the day
    private double pc;  // previous close price

    // Getters & setters
    public double getC() { return c; }
    public void setC(double c) { this.c = c; }

    public double getD() { return d; }
    public void setD(double d) { this.d = d; }

    public double getDp() { return dp; }
    public void setDp(double dp) { this.dp = dp; }

    public double getH() { return h; }
    public void setH(double h) { this.h = h; }

    public double getL() { return l; }
    public void setL(double l) { this.l = l; }

    public double getO() { return o; }
    public void setO(double o) { this.o = o; }

    public double getPc() { return pc; }
    public void setPc(double pc) { this.pc = pc; }
}
