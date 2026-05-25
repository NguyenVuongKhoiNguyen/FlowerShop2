package com.poly.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductRequest {
	private String name;
	private String image;
	private Double costPrice;
	private Double retailPercentage;
	private Boolean available;
	private Integer amount;
	private long sales;
	private Integer categoryId;
}
