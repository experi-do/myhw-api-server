package com.sk.skala.stockapi.service;

import com.sk.skala.stockapi.data.dto.RankingDto;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final PlayerRepository playerRepository;
    private final PlayerStockRepository playerStockRepository;

    @Transactional(readOnly = true)
    public List<RankingDto> getRanking() {
        List<Player> players = playerRepository.findAll();
        List<PlayerStock> playerStocks = playerStockRepository.findAll();

        List<RankingDto> rankings = new ArrayList<>();
        for (Player player : players) {
            double stockAssets = playerStocks.stream()
                    .filter(ps -> ps.getPlayer().getPlayerId().equals(player.getPlayerId()))
                    .mapToDouble(ps -> ps.getQuantity() * ps.getStock().getStockPrice())
                    .sum();

            double totalAssets = player.getPlayerMoney() + stockAssets;
            double profitRate = 0;
            if (player.getInitialMoney() > 0) {
                profitRate = (totalAssets - player.getInitialMoney()) / player.getInitialMoney();
            }

            RankingDto rankingDto = new RankingDto();
            rankingDto.setPlayerId(player.getPlayerId());
            rankingDto.setTotalAssets(totalAssets);
            rankingDto.setProfitRate(profitRate);
            rankings.add(rankingDto);
        }

        rankings.sort(Comparator.comparingDouble(RankingDto::getProfitRate).reversed());

        int rank = 1;
        for (RankingDto ranking : rankings) {
            ranking.setRank(rank++);
        }

        return rankings;
    }
}
