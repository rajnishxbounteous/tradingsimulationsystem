package com.example.tradingsimulationsystem.domain;

public class MarketStatus {

    private boolean open;
    private String message;

    public MarketStatus(boolean open, String message) {
        this.open = open;
        this.message = message;
    }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
