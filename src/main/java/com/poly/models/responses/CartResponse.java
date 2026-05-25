package com.poly.models.responses;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CartResponse {
    private Long id;
    private LocalDate createDate;
    private String username;
    private String fullname;
    private Double total;
    private List<ItemResponse> itemResponses;
}