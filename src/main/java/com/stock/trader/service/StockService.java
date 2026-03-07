package com.stock.trader.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StockService {
    private final WebClient webClient = WebClient.create("https://www.alphavantage.co");
    private final String API_KEY = "1JODJUV5UQ859LYX";
    public String getDailyStock(String symbol) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/query").queryParam("function", "TIME_SERIES_DAILY").queryParam("symbol", symbol).queryParam("apikey", API_KEY).build()).retrieve().bodyToMono(String.class).block();
    }
    public String searchSymbol(String keywords) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/query")
                    .queryParam("function", "SYMBOL_SEARCH")
                    .queryParam("keyword", keywords)
                    .queryParam("apikey", API_KEY)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
