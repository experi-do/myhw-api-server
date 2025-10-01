package com.sk.skala.stockapi.data.dto;

import com.sk.skala.stockapi.data.table.PlayerWatchlist;
import com.sk.skala.stockapi.data.table.Stock;
import lombok.Data;

@Data
public class WatchlistDto {
    private Long id;
    private Long stockId;
    private String stockName;
    private Double stockPrice;

    public WatchlistDto(PlayerWatchlist playerWatchlist) {
        this.id = playerWatchlist.getId();
        Stock stock = playerWatchlist.getStock();
        this.stockId = stock.getId();
        this.stockName = stock.getStockName();
        this.stockPrice = stock.getStockPrice();
    }
}
