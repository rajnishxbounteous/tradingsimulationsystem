package com.example.tradingsimulationsystem.dto;

public class MarketSummaryDTO {

    private long upCount;
    private long downCount;
    private double averagePercentChange;

    public MarketSummaryDTO(long upCount, long downCount, double averagePercentChange) {
        this.upCount = upCount;
        this.downCount = downCount;
        this.averagePercentChange = averagePercentChange;
    }

    public long getUpCount() {
        return upCount;
    }

    public void setUpCount(long upCount) {
        this.upCount = upCount;
    }

    public long getDownCount() {
        return downCount;
    }

    public void setDownCount(long downCount) {
        this.downCount = downCount;
    }

    public double getAveragePercentChange() {
        return averagePercentChange;
    }

    public void setAveragePercentChange(double averagePercentChange) {
        this.averagePercentChange = averagePercentChange;
    }
}
