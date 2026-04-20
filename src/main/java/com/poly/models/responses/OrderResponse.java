package com.poly.models.responses;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String username;
    private LocalDate createDate;
    private String fullname;
    private String phone;
    private String address;
    private String status;
    private Double total;
    private List<OrderDetailResponse> orderDetails;
}
