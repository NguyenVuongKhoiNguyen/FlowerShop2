package com.poly.models.requests;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    private String username;
    private String fullname;
    private String phone;
    private String address;
    private String status;
    private List<OrderDetailRequest> orderDetailRequests;  
}
