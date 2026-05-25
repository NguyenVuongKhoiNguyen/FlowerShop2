package com.poly.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.OrderRequest;
import com.poly.models.responses.OrderResponse;
import com.poly.models.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard/order")
public class DashboardOrderController {
	
	private final OrderService orderService;
	
	@PutMapping("{id}")
	public OrderResponse update(@RequestBody OrderRequest request, @PathVariable Long id) {
		return orderService.update(id, request);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable Long id) {
		orderService.delete(id);
	}
}
