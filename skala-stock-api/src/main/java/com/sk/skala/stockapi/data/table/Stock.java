package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 거래 가능한 주식을 나타냅니다.
 * 이 클래스는 데이터베이스의 "Stock" 테이블에 매핑되는 JPA 엔티티입니다.
 */
@Data
@Entity
@NoArgsConstructor
public class Stock {

	/**
	 * 주식의 고유 식별자입니다.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 주식의 이름입니다.
	 */
	private String stockName;

	/**
	 * 주식의 현재 가격입니다.
	 */
	private Double stockPrice;

	/**
	 * 이름과 가격으로 새 주식을 생성하는 생성자입니다.
	 * @param name 주식의 이름.
	 * @param price 주식의 가격.
	 */
	public Stock(String name, Double price) {
		this.stockName = name;
		this.stockPrice = price;
	}
}