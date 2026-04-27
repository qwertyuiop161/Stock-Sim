package com.stock.trader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.stock.trader.service.StockService;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.api.core.ApiFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class StockController {
    private final StockService stockService;
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    @GetMapping("/stock")
    public ResponseEntity<ApiResponse<String>> getStock(@RequestParam(defaultValue = "AAPL") String symbol) {
        try {
            logger.info("fetching data for {}", symbol);
            String data = stockService.getDailyStock(symbol);
            logger.debug("Successfully got THE data for {}", symbol);
            return ResponseEntity.ok(new ApiResponse<>(true, data, null, 200));
        } catch (Exception e) {
            logger.error("Error dumdum: {}", symbol, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, e.getMessage(), 400));
        }
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<String>> search(@RequestParam String q) {
        try {
            logger.info("Searching for blah blah : {}", q);
            String data = stockService.searchSymbol(q);
            return ResponseEntity.ok(new ApiResponse<>(true, data, null, 200));
        } catch (Exception e) {
            logger.error("fix dis idiot", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, e.getMessage(), 400));
        }
    }
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<String>> getOverview(@RequestParam(defaultValue="AAPL") String symbol) {
        try {
            logger.info("FETching this for u lazu bum since u cant do it urself {}", symbol);
            String data =stockService.getOverview(symbol);
            return ResponseEntity.ok(new ApiResponse<>(true, data, null, 200));
        } catch (Exception e) {
            logger.error("ha nerd u suck at coding: {}", symbol, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, e.getMessage(), 400));
        }
    }
    @PostMapping("/limit-order")
    public ResponseEntity<ApiResponse<String>> placeLimitOrder(@RequestParam String symbol, @RequestParam String type, @RequestParam double limitPrice, @RequestParam double quantity, @RequestParam String userId) {
        try {
            logger.info("making limit order for user {} on symbol {}", userId, symbol);
            Map<String, Object> order = new HashMap<>();
            order.put("userId", userId);
            order.put("symbol", symbol);
            order.put("type", type);
            order.put("limitPrice", limitPrice);
            order.put("quantity", quantity);
            order.put("timestamp", System.currentTimeMillis());
            stockService.getFirestore().collection("orders").add(order);
            logger.info("done wow u did smtn right shocking {}", userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "order placed", null, 200));
        } catch (Exception e) {
            logger.error("error nerd ha", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, "Error: " + e.getMessage(), 400));
        }
    }
    @GetMapping("/limit-orders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLimitOrders(@RequestParam String userId) {
        try {
            logger.info("Fetching limit orders or user {}", userId);
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
            logger.debug("found {} orders for user {}", orders.size(), userId);
            return ResponseEntity.ok(new ApiResponse<>(true, orders, null, 200));
        } catch (Exception e) {
            logger.error("Error for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, List.of(), e.getMessage(), 400));
        }
    }
    @DeleteMapping("/limit-order/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelLimitOrder(@PathVariable String orderId) {
        try {            if (orderId == null || orderId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, "Invalid order ID", 400));
            }            logger.info("cancel order {}", orderId);
            stockService.getFirestore().collection("orders").document(orderId).delete();
            logger.info("canceled order {}", orderId);
            return ResponseEntity.ok(new ApiResponse<>(true, "order canceled", null, 200));
        } catch (Exception e) {
            logger.error("error canceling: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, e.getMessage(), 400));
        }
    }
    @PostMapping("/check-orders")
    public ResponseEntity<ApiResponse<String>> checkOrders(@RequestParam String userId, @RequestParam String symbol, @RequestParam double price) {
        try {
            logger.info("Checking orders for user: {} symbol: {} price: {}", userId, symbol, price);
            stockService.checkAndExecuteLimitOrders(userId, price, symbol);
            return ResponseEntity.ok(new ApiResponse<>(true, "Orders checked", null, 200));
        } catch (Exception e) {
            logger.error("Error checking orders", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, null, e.getMessage(), 400));
        }
    }
    public static class ApiResponse<T> {
        private boolean success;
        private T data;
        private String error;
        private int code;
        public ApiResponse(boolean success, T data, String error, int code) {
            this.success = success;
            this.data = data;
            this.error = error;
            this.code = code;
        }

        public boolean isSuccess() {
            return success;
        }
        public T getData() {
            return data;
        }
        public String getError() {
            return error;
        }
        public int getCode() {
            return code;
        }
    }
}

