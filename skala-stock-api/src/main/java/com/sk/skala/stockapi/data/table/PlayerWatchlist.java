package com.sk.skala.stockapi.data.table;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 플레이어의 관심 주식 목록을 나타냅니다.
 * 이 클래스는 데이터베이스의 "PlayerWatchlist" 테이블에 매핑되는 JPA 엔티티입니다.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name="player_watchlist")
public class PlayerWatchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="stock_id", nullable=false)
    private Stock stock;

    public PlayerWatchlist(Player player, Stock stock) {
        this.player = player;
        this.stock = stock;
    }
}
