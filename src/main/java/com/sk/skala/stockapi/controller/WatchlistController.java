package com.sk.skala.stockapi.controller;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.request.WatchlistRequest;
import com.sk.skala.stockapi.service.PlayerWatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final PlayerWatchlistService playerWatchlistService;

    @PostMapping("/add")
    public Response addStockToWatchlist(@RequestBody WatchlistRequest request) {
        return playerWatchlistService.addStockToWatchlist(request.getStockId());
    }

    @DeleteMapping("/remove")
    public Response removeStockFromWatchlist(@RequestBody WatchlistRequest request) {
        return playerWatchlistService.removeStockFromWatchlist(request.getStockId());
    }

    @GetMapping
    public Response getWatchlist() {
        return playerWatchlistService.getWatchlist();
    }
}
