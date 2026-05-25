package com.poly.models.services;

import com.poly.models.requests.AccountRequest;
import com.poly.models.requests.AuthRequest;
import com.poly.models.requests.AuthResponse;
import com.poly.models.requests.TokenRequest;
import com.poly.models.responses.AccountResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.responses.TokenResponse;

public interface AccountService {
	AuthResponse login(AuthRequest authRequest);
	TokenResponse loginWithGoogle(TokenRequest tokenResponse);
	AccountResponse create(AccountRequest request);
	AccountResponse update(AccountRequest request);
	void delete(String username);
	AccountResponse findById(String username);
	AccountResponse findByEmail(String email);
	PageResponse<AccountResponse> filterAndPaginateAccounts(
			String username,
            String fullname,
            String email,
            Boolean activated,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize);
}
