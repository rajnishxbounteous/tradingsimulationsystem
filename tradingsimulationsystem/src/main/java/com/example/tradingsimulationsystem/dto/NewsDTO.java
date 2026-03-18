package com.example.tradingsimulationsystem.dto;

public class NewsDTO {

    private String headline;
    private String summary;
    private String url;

    public NewsDTO() {}

    public NewsDTO(String headline, String summary, String url) {
        this.headline = headline;
        this.summary = summary;
        this.url = url;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
