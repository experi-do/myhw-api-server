package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주식 거래 게임의 플레이어를 나타냅니다.
 * 이 클래스는 데이터베이스의 "Player" 테이블에 매핑되는 JPA 엔티티입니다.
 */
@Data
@Entity
@NoArgsConstructor
public class Player {

	/**
	 * 플레이어의 고유 식별자입니다.
	 */
	@Id
	private String playerId;

	/**
	 * 인증을 위한 플레이어의 비밀번호입니다.
	 */
	private String playerPassword;

	/**
	 * 플레이어가 거래에 사용할 수 있는 돈의 양입니다.
	 */
	private double playerMoney;

	/**
	 * 플레이어의 초기 자본금입니다.
	 */
	private double initialMoney;

	/**
	 * ID와 초기 금액으로 새 플레이어를 생성하는 생성자입니다.
	 * @param id 플레이어의 고유 ID.
	 * @param money 플레이어의 초기 금액.
	 */
	public Player(String id, double money) {
		this.playerId = id;
		this.playerMoney = money;
		this.initialMoney = money;
	}

}
