package com.sk.skala.stockapi.data.dto;

import lombok.Data;

@Data
public class RankingDto {
    private int rank;
    private String playerId;
    private double profitRate;
    private double totalAssets;
}
