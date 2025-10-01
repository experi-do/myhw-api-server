package com.sk.skala.stockapi.service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.data.table.PlayerWatchlist;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerWatchlistRepository;
import com.sk.skala.stockapi.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerWatchlistService {

    private final PlayerWatchlistRepository playerWatchlistRepository;
    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final SessionHandler sessionHandler;

    @Transactional
    public Response addStockToWatchlist(Long stockId) {
        String playerId = sessionHandler.getPlayerId();

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Stock not found"));

        Optional<PlayerWatchlist> existingWatchlist = playerWatchlistRepository.findByPlayer_PlayerIdAndStock_Id(playerId, stockId);
        if (existingWatchlist.isPresent()) {
            throw new ResponseException(Error.DATA_DUPLICATED, "Stock already in watchlist");
        }

        PlayerWatchlist playerWatchlist = new PlayerWatchlist(player, stock);
        playerWatchlistRepository.save(playerWatchlist);

        return new Response();
    }

    @Transactional
    public Response removeStockFromWatchlist(Long stockId) {
        String playerId = sessionHandler.getPlayerId();

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player not found"));
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Stock not found"));

        PlayerWatchlist playerWatchlist = playerWatchlistRepository.findByPlayer_PlayerIdAndStock_Id(playerId, stockId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Stock not found in watchlist"));

        playerWatchlistRepository.delete(playerWatchlist);

        return new Response();
    }

    @Transactional(readOnly = true)
    public Response getWatchlist() {
        String playerId = sessionHandler.getPlayerId();

        List<PlayerWatchlist> watchlist = playerWatchlistRepository.findByPlayer_PlayerId(playerId);

        List<com.sk.skala.stockapi.data.dto.WatchlistDto> watchlistDto = watchlist.stream()
                .map(com.sk.skala.stockapi.data.dto.WatchlistDto::new)
                .collect(java.util.stream.Collectors.toList());

        Response response = new Response();
        response.setBody(watchlistDto);
        return response;
    }
}
