package com.poly.controllers;

import java.time.LocalDate;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.OrderRequest;
import com.poly.models.responses.OrderResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
	
	private final OrderService orderService;
	
	@PostMapping
	public OrderResponse create(@RequestBody OrderRequest request) {
		return orderService.create(request);
	}
	
	@PutMapping("{id}")
	public OrderResponse update(@RequestBody OrderRequest request, @PathVariable Long id) {
		return orderService.update(id, request);
	}
	
	public PageResponse<OrderResponse> filterOrders(
			@RequestParam(required = false) String username,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @DefaultValue(value = "DESC") String sortOrder,
            @DefaultValue(value = "1") Integer pageNumber,
            @DefaultValue(value = "5") Integer pageSize
			) {
		return orderService.filterAndPaginateOrders(username, fullname, fromDate, toDate, sortOrder, pageNumber, pageSize);
	}
}
