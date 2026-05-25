package com.poly.models.requests;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountRequest {
	private String username;
    private String password;
    private String fullname;
    private String email;
    private String photo;
    private String phone;
    private String address;
    private Boolean activated;
    private List<RoleRequest> roleRequests;
}
