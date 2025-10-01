package com.sk.skala.stockapi.controller;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.service.StockService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/list")
    public Response getAllStocks(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int count) {
        return stockService.getAllStocks(offset, count);
    }

    @GetMapping("/{id}")
    public Response getStockById(@PathVariable Long id) {
        return stockService.getStockById(id);
    }

    @PostMapping
    public Response createStock(@RequestBody Stock stock) {
        return stockService.createStock(stock);
    }

    @PutMapping
    public Response updateStock(@RequestBody Stock stock) {
        return stockService.updateStock(stock);
    }

    @DeleteMapping
    public Response deleteStock(@RequestBody Stock stock) {
        return stockService.deleteStock(stock);
    }
}
