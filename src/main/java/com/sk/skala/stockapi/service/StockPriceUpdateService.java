package com.sk.skala.stockapi.service;

import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StockPriceUpdateService {

    private final StockRepository stockRepository;
    private final Random random = new Random();

    @Transactional
    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void updateStockPrices() {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            double currentPrice = stock.getStockPrice();
            double changePercent = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5% 변동
            double newPrice = currentPrice * (1 + changePercent);
            stock.setStockPrice(Math.round(newPrice * 100.0) / 100.0); // 소수점 둘째 자리까지 반올림
        }
        stockRepository.saveAll(stocks);
    }
}
