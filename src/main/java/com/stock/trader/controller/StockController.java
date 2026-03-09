package com.stock.trader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stock.trader.service.StockService;

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
    public String search(@RequestParam String keywords) {
        return stockService.searchSymbol(keywords);
    }
}