package com.poly.models.services;

import java.time.LocalDate;

import com.poly.models.requests.CartRequest;
import com.poly.models.responses.CartResponse;
import com.poly.models.responses.PageResponse;

public interface CartService {
	CartResponse create(CartRequest request);
	CartResponse update(Long id, CartRequest request);
	void delete(Long id);
	CartResponse findById(Long id);
	PageResponse<CartResponse> filterAndPaginateCart(
			String username,
            String fullname,
            LocalDate fromDate,
            LocalDate toDate,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize);
}
