package com.poly.models.services;

import java.time.LocalDate;

import com.poly.models.requests.OrderRequest;
import com.poly.models.responses.OrderResponse;
import com.poly.models.responses.PageResponse;

public interface OrderService {
	OrderResponse create(OrderRequest request);
	OrderResponse update(Long id, OrderRequest request);
	void delete(Long id);
	OrderResponse findById(Long id);
	PageResponse<OrderResponse> filterAndPaginateOrders(
			String username,
            String fullname,
            LocalDate fromDate,
            LocalDate toDate,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize);
}
