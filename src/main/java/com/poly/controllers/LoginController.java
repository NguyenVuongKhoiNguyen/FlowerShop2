package com.poly.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.AuthRequest;
import com.poly.models.requests.AuthResponse;
import com.poly.models.requests.TokenRequest;
import com.poly.models.responses.TokenResponse;
import com.poly.models.services.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
	
	private final AccountService accountService;
	
	@PostMapping
	public AuthResponse login(@RequestBody AuthRequest request) {
		return accountService.login(request);
	}
	
	@PostMapping("/google")
	public TokenResponse loginWithGoogle(@RequestBody TokenRequest tokenRequest) {
		return accountService.loginWithGoogle(tokenRequest);
	}
}
