package com.sk.skala.stockapi.data.table;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 플레이어의 주식 보유 정보를 나타냅니다.
 * 이 클래스는 "PlayerStock" 테이블에 매핑되는 JPA 엔티티입니다.
 */
@Entity
@Data
@NoArgsConstructor
public class PlayerStock {

	/**
	 * 이 주식 보유 기록의 고유 식별자입니다.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 주식을 소유한 플레이어입니다. (지연 로딩)
	 */
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="player_id")
	private Player player;

	/**
	 * 보유하고 있는 주식입니다. (지연 로딩)
	 */
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="stock_id")
	private Stock stock;

	/**
	 * 플레이어가 보유한 주식의 수량입니다.
	 */
	private int quantity;

	/**
	 * 새로운 플레이어 주식 보유 기록을 생성하는 생성자입니다.
	 * @param player 주식을 소유한 플레이어.
	 * @param stock 보유하고 있는 주식.
	 * @param quantity 보유 주식 수량.
	 */
	public PlayerStock(Player player, Stock stock, int quantity) {
		this.player = player;
		this.stock = stock;
		this.quantity = quantity;
	}
}
