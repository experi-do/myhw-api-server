package com.sk.skala.stockapi.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerStockDto {

    private Long stockId;
    private String stockName;
    private Double stockPrice;
    private Integer quantity;
}