package com.poly.models.responses;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountResponse {
	private String username;
	private String fullname;
	private String email;
	private String photo;
	private String address;
	private String phone;
	private LocalDate createDate;
	private Boolean activated;
	private List<RoleResponse> roleResponses;
}
