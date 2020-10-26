package com.example.simplecalculator.NetworkUtils;

public class LatestCurrencyRequest {
    private String accessKey;
    private String symbols;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }
}
