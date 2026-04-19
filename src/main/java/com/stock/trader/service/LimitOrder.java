package com.stock.trader.service;

public class LimitOrder {
    private String id;
    private String userId;
    private String symbol;
    private String type;
    private double limitPrice;
    private double quantity;
    private long timestamp;

    public LimitOrder() {}

    public LimitOrder(String id, String userId, String symbol, String type, double limitPrice, double quantity) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.type = type;
        this.limitPrice = limitPrice;
        this.quantity = quantity;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getLimitPrice() {
        return limitPrice;
    }
    public void setLimitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}