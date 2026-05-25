package com.poly.models.responses;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProductResponse {
	private Integer id;
	private String name;
	private String image;
	private Double costPrice;
	private Double retailPercentage;
	private LocalDate createDate;
	private Boolean available;
	private Integer amount;
	private Integer sales;
	private Integer categoryId;
}
