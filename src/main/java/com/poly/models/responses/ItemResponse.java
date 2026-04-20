package com.poly.models.responses;

import lombok.Data;

@Data
public class ItemResponse {
    private Integer productId;
    private String productImage;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
}