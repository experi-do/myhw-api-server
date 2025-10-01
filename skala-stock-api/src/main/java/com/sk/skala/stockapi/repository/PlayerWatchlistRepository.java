package com.sk.skala.stockapi.repository;

import com.sk.skala.stockapi.data.table.PlayerWatchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerWatchlistRepository extends JpaRepository<PlayerWatchlist, Long> {
    List<PlayerWatchlist> findByPlayer_PlayerId(String playerId);
    Optional<PlayerWatchlist> findByPlayer_PlayerIdAndStock_Id(String playerId, Long stockId);
}