package com.poly.models.requests;

import lombok.Data;

@Data
public class OrderDetailRequest {
    private Integer productId;
    private Integer quantity;
}
