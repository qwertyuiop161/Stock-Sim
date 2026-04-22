package com.stock.trader.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.api.core.ApiFuture;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Service
public class StockService {
    private final WebClient webClient;
    private final Firestore firestore;
    
    public StockService(Firestore firestore) {
        this.firestore = firestore;
        this.webClient = WebClient.create("https://www.alphavantage.co");
    }

    private String getTimeBasedApiKey() {
        long currentMinute = System.currentTimeMillis() / (60 * 1000);
        return "minute_" + currentMinute;
    }

    public String getDailyStock(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/query").queryParam("function", "TIME_SERIES_DAILY").queryParam("symbol", symbol).queryParam("apikey", getTimeBasedApiKey()).build())
        .retrieve()
        .bodyToMono(String.class)
        .block();
    }

    public String searchSymbol(String keywords) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "SYMBOL_SEARCH")
                    .queryParam("keywords", keywords)
                    .queryParam("apikey", getTimeBasedApiKey())
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getOverview(String symbol) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "OVERVIEW")
                    .queryParam("symbol", symbol)
                    .queryParam("apikey", getTimeBasedApiKey())
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); 
    }

    public void checkAndExecuteLimitOrders(String userId, double currentPrice, String symbol) {
        try {
            ApiFuture<com.google.cloud.firestore.QuerySnapshot> future = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("symbol", symbol)
                .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                Map<String, Object> orderData = doc.getData();
                String type = (String) orderData.get("type");
                double limitPrice = ((Number) orderData.get("limitPrice")).doubleValue();
                double quantity = ((Number) orderData.get("quantity")).doubleValue();
                long timestamp = ((Number) orderData.get("timestamp")).longValue();
                String orderId = doc.getId();
                long timeoutMs = 24 * 60 * 60 * 1000;
                if (System.currentTimeMillis() - timestamp > timeoutMs) {
                    firestore.collection("orders").document(orderId).delete();
                    continue;
                }
                boolean shouldExecute = false;
                if ("BUY".equals(type) && currentPrice <= limitPrice) {
                    shouldExecute = true;
                } else if ("SELL".equals(type) && currentPrice >= limitPrice) {
                    shouldExecute = true;
                }
                if (shouldExecute) {
                    DocumentReference userRef = firestore.collection("users").document(userId);
                    ApiFuture<com.google.cloud.firestore.DocumentSnapshot> userFuture = userRef.get();
                    Map<String, Object> userData = userFuture.get().getData();
                    Map<String, Object> tradingSession = (Map<String, Object>) userData.get("tradingSession");
                    double currentCash = ((Number) tradingSession.get("currentCash")).doubleValue();
                    List<Map<String, Object>> portfolio = (List<Map<String, Object>>) userData.get("portfolio");
                    if ("BUY".equals(type)) {
                        if (currentCash >= quantity) {
                            Map<String, Object> trade = new HashMap<>();
                            trade.put("symbol", symbol);
                            trade.put("price", currentPrice);
                            trade.put("shares", quantity / currentPrice);
                            trade.put("dollars", quantity);
                            trade.put("type", "BUY");
                            trade.put("time", System.currentTimeMillis());
                            portfolio.add(trade);
                            currentCash -= quantity;
                        }
                    } else if ("SELL".equals(type)) {
                        double sharesOwned = 0;
                        for (Map<String, Object> trade : portfolio) {
                            if (symbol.equals(trade.get("symbol")) && "BUY".equals(trade.get("type"))) {
                                sharesOwned += ((Number) trade.get("shares")).doubleValue();
                            } else if (symbol.equals(trade.get("symbol")) && "SELL".equals(trade.get("type"))) {
                                sharesOwned -= ((Number) trade.get("shares")).doubleValue();
                            }
                        }
                        double sharesToSell = quantity / currentPrice;
                        if (sharesOwned >= sharesToSell) {
                            Map<String, Object> trade = new HashMap<>();
                            trade.put("symbol", symbol);
                            trade.put("price", currentPrice);
                            trade.put("shares", sharesToSell);
                            trade.put("dollars", quantity);
                            trade.put("type", "SELL");
                            trade.put("time", System.currentTimeMillis());
                            portfolio.add(trade);
                            currentCash += quantity;
                        }
                    }
                    userRef.update("portfolio", portfolio);
                    Map<String, Object> updatedSession = new HashMap<>(tradingSession);
                    updatedSession.put("currentCash", currentCash);
                    userRef.update("tradingSession", updatedSession);
                    firestore.collection("orders").document(orderId).delete();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public Firestore getFirestore() {
        return firestore;
    }
}