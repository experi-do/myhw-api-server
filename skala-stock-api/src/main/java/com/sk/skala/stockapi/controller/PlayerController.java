package com.sk.skala.stockapi.controller;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService playerService;

    // 전체 플레이어 목록 조회 API
    @GetMapping("/list")
    public Response getAllPlayers(@RequestParam(value="offset", defaultValue = "0") int offset, @RequestParam(value="count", defaultValue = "10") int count) {
        return playerService.getAllPlayers(offset, count);
    }

    // 단일 플레이어 상세 조회 API
    @GetMapping("/{playerId}")
    public Response getPlayerById(@PathVariable("playerId") String playerId) {
        return playerService.getPlayerById(playerId);
    }

    // 플레이어 등록
    @PostMapping
    public Response createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    // 플레이어 로그인
    @PostMapping("/login")
    public Response loginPlayer(@RequestBody PlayerSession playerSession) {
        return playerService.loginPlayer(playerSession);
    }

    // 플레이어 정보 업데이트
    @PutMapping
    public Response updatePlayer(@RequestBody Player player) {
        return playerService.updatePlayer(player);
    }

    // 플레이어 삭제
    @DeleteMapping
    public Response deletePlayer(@RequestBody Player player) {
        return playerService.deletePlayer(player);
    }

    @PostMapping("/buy")
    public Response buyPlayerStock(@RequestBody StockOrder order) {
        return playerService.buyPlayerStock(order);
    }

    @PostMapping("/sell")
    public Response sellPlayerStock(@RequestBody StockOrder order) {
        return playerService.sellPlayerStock(order);
    }
}
