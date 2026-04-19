package com.stock.trader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stock.trader.service.StockService;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import com.google.api.core.ApiFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StockController {
    private final StockService stockService;
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    @GetMapping("/stock")
    public String getStock(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getDailyStock(symbol);
    }
    @GetMapping("/search")
    public String search(@RequestParam String q) {
        return stockService.searchSymbol(q);
    }
    @GetMapping("/overview")
    public String getOverview(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getOverview(symbol);
    }
    @PostMapping("/limit-order")
    public String placeLimitOrder(@RequestParam String symbol, @RequestParam String type, @RequestParam double limitPrice, @RequestParam double quantity, @RequestParam String userId) {
        try {
            Map<String, Object> order = new HashMap<>();
            order.put("userId", userId);
            order.put("symbol", symbol);
            order.put("type", type);
            order.put("limitPrice", limitPrice);
            order.put("quantity", quantity);
            order.put("timestamp", System.currentTimeMillis());
            stockService.getFirestore().collection("orders").add(order);
            return "Order placed";
        } catch (Exception e) {
            return "Error: " +e.getMessage();
        }
    }
    @GetMapping("/limit-orders")
    public List<Map<String, Object>> getLimitOrders(@RequestParam String userId) {
        try {
            ApiFuture<com.google.cloud.firestore.QuerySnapshot> future = stockService.getFirestore().collection("orders")
                .whereEqualTo("userId", userId)
                .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            List<Map<String, Object>> orders = new ArrayList<>();
            for (QueryDocumentSnapshot doc : docs) {
                Map<String, Object> order = doc.getData();
                order.put("id", doc.getId());
                orders.add(order);
            }
            return orders;
        } catch (Exception e) {
            return List.of();
        }
    }
    @DeleteMapping("/limit-order/{orderId}")
    public String cancelLimitOrder(@PathVariable String orderId) {
        try {
            stockService.getFirestore().collection("orders").document(orderId).delete();
            return "Order canceled";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    @PostMapping("/check-orders")
    public void checkOrders(@RequestParam String userId, @RequestParam String symbol, @RequestParam double price) {
        stockService.checkAndExecuteLimitOrders(userId, price, symbol);
    }
}