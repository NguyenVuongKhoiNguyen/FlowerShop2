package com.poly.models.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
	private String token;
	private String fullname;
	private String photo;
}
