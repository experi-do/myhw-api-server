package com.sk.skala.stockapi.service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.PlayerStockDto;
import com.sk.skala.stockapi.data.dto.PlayerStockListDto;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerStockRepository;
import com.sk.skala.stockapi.repository.StockRepository;
import com.sk.skala.stockapi.tools.StringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final StockRepository stockRepository;
    private final PlayerStockRepository playerStockRepository;
    private final SessionHandler sessionHandler;

    // 전체 플레이어 목록 조회
    public Response getAllPlayers(int offset, int count) {

        Pageable pageable = PageRequest.of(offset, count, Sort.by(Sort.Order.asc("playerId")));
        Page<Player> paged = playerRepository.findAll(pageable);

        PagedList pagedList = new PagedList();
        pagedList.setTotal(paged.getTotalElements());
        pagedList.setCount(paged.getNumberOfElements());
        pagedList.setOffset(offset);
        pagedList.setList(paged.getContent());

        Response response = new Response();
        response.setBody(pagedList);
        return response;
    }

    // 단일 플레이어 및 주식 목록 조회
    @Transactional(readOnly=true)
    public Response getPlayerById(String playerId) {
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);
        Response response = new Response();

        if (optionalPlayer.isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND, "Player not found");
        }
        Player player = optionalPlayer.get();

        List<PlayerStock> playerStockList = playerStockRepository.findByPlayer_PlayerId(playerId);

        List<PlayerStockDto> stockDtos = playerStockList.stream()
                .map(playerStock -> PlayerStockDto.builder().stockId(playerStock.getStock().getId())
                        .stockName(playerStock.getStock().getStockName())
                        .stockPrice(playerStock.getStock().getStockPrice()).quantity(playerStock.getQuantity()).build())
                .collect(Collectors.toList());

        PlayerStockListDto playerStockListDto = PlayerStockListDto.builder().playerId(player.getPlayerId())
                .playerMoney(player.getPlayerMoney()).stocks(stockDtos)
                .build();

        response.setBody(playerStockListDto);

        return response;
    }

    // 플레이어 생성
    public Response createPlayer(Player playerSession) {
        if (StringTool.isAnyEmpty(playerSession.getPlayerId())
                || StringTool.isAnyEmpty(playerSession.getPlayerPassword())) {
            throw new ParameterException("playerId", "playerPassword");
        }

        Optional<Player> option = playerRepository.findById(playerSession.getPlayerId());
        if (option.isPresent()) {
            throw new ResponseException(Error.DATA_DUPLICATED);
        }
        Player player = new Player();
        player.setPlayerId(playerSession.getPlayerId());
        player.setPlayerPassword(playerSession.getPlayerPassword());
        player.setPlayerMoney(100000);
        player.setInitialMoney(100000);

        playerRepository.save(player);

        return new Response();
    }

    // 플레이어 로그인
    public Response loginPlayer(PlayerSession playerSession) {
        Response response = new Response();

        if (StringTool.isAnyEmpty(playerSession.getPlayerId())
                || StringTool.isAnyEmpty(playerSession.getPlayerPassword())) {
            throw new ParameterException("playerId", "playerPassword");
        }

        Optional<Player> optionalPlayer = playerRepository.findById(playerSession.getPlayerId());
        if (optionalPlayer.isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND);
        }

        Player player = optionalPlayer.get();
        if (player.getPlayerPassword().equals(playerSession.getPlayerPassword())) {
            sessionHandler.storeAccessToken(playerSession);
        }
        else {
            throw new ResponseException(Error.NOT_AUTHENTICATED);
        }

        player.setPlayerPassword(null);
        response.setBody(player);
        return response;
    }

    // 플레이어 정보 업데이트
    public Response updatePlayer(Player plyr) {
        Response response = new Response();

        if (StringTool.isAnyEmpty(plyr.getPlayerId()) || plyr.getPlayerMoney() <= 0) {
            throw new ResponseException(Error.DATA_NOT_FOUND);
        }

        Optional<Player> optionalPlayer = playerRepository.findById(plyr.getPlayerId());
        if (optionalPlayer.isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND);
        }

        Player player = optionalPlayer.get();
        player.setPlayerMoney(plyr.getPlayerMoney());
        playerRepository.save(player);
        response.setBody(player);
        return response;
    }

    // 플레이어 삭제
    public Response deletePlayer(Player player) {
        Optional<Player> optionalPlayer = playerRepository.findById(player.getPlayerId());
        if (optionalPlayer.isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND);
        }

        playerRepository.deleteById(player.getPlayerId());
        return new Response();
    }

    // 주식 매수
    @Transactional
    public Response buyPlayerStock(StockOrder order) {
        String playerId = sessionHandler.getPlayerId();
        logger.info("buyPlayerStock called for playerId: {}, order: {}", playerId, order);

        Player player = playerRepository.findById(playerId).orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));
        Stock stock = stockRepository.findById(order.getStockId()).orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        logger.info("Player money before purchase: {}", player.getPlayerMoney());

        if (player.getPlayerMoney() - order.getStockQuantity() * stock.getStockPrice() >= 0) {
            double playerMoney = player.getPlayerMoney() - order.getStockQuantity() * stock.getStockPrice();
            player.setPlayerMoney(playerMoney);
            logger.info("Player money after purchase: {}", player.getPlayerMoney());
        }
        else {
            throw new ResponseException(Error.INSUFFICIENT_FUNDS);
        }

        Optional<PlayerStock> optionalPlayerStock = playerStockRepository.findByPlayerAndStock(player, stock);
        if (optionalPlayerStock.isEmpty()) {
            PlayerStock playerStock = new PlayerStock();
            playerStock.setStock(stock);
            playerStock.setPlayer(player);
            playerStock.setQuantity(order.getStockQuantity());

            playerStockRepository.save(playerStock);
        }
        else {
            PlayerStock playerStock = optionalPlayerStock.get();

            int stockQuantity = playerStock.getQuantity() + order.getStockQuantity();
            playerStock.setQuantity(stockQuantity);

            playerStockRepository.save(playerStock);
        }
        return new Response();
    }

    @Transactional
    public Response sellPlayerStock(StockOrder order) {
        String playerId = sessionHandler.getPlayerId();

        Player player = playerRepository.findById(playerId).orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));
        Stock stock = stockRepository.findById(order.getStockId()).orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        PlayerStock playerStock = playerStockRepository.findByPlayerAndStock(player, stock)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player does not own this stock"));

        if (playerStock.getQuantity() < order.getStockQuantity()) {
            throw new ResponseException(Error.INSUFFICIENT_QUANTITY);
        }
        else if (playerStock.getQuantity() == order.getStockQuantity()) {
            playerStockRepository.delete(playerStock);
        }
        else {
            playerStock.setQuantity(playerStock.getQuantity() - order.getStockQuantity());
            playerStockRepository.save(playerStock);
        }

        double playerMoney = player.getPlayerMoney() + order.getStockQuantity() * stock.getStockPrice();
        player.setPlayerMoney(playerMoney);

        return new Response();
    }

}
