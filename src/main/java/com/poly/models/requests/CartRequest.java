package com.poly.models.requests;

import java.util.List;

import lombok.Data;

@Data
public class CartRequest {
    private String username;          
    private List<ItemRequest> itemRequests;
}