package com.sk.skala.stockapi.data.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class PlayerStockListDto {

    private String playerId;
    private Double playerMoney;
    private List<PlayerStockDto> stocks;
}