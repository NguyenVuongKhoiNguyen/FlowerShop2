package com.poly.controllers;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.CartRequest;
import com.poly.models.responses.CartResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
	
	private final CartService cartService;
	
	@PostMapping
	public CartResponse create(@RequestBody CartRequest request) {
		
		return cartService.create(request);	
	}
	
	@PutMapping("{id}")
	public CartResponse update(@RequestBody CartRequest request, @PathVariable Long id) {
		
		return cartService.update(id, request);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable Long id) {
		
		cartService.delete(id);
	}
	
	public PageResponse<CartResponse> filterCarts(
				@RequestParam(required = false) String username,
				@RequestParam(required = false) String fullname,
				@RequestParam(required = false) LocalDate fromDate,
				@RequestParam(required = false) LocalDate toDate,
				@RequestParam(defaultValue = "DESC") String sortOrder,
				@RequestParam(defaultValue = "1") Integer pageNumber,
				@RequestParam(defaultValue = "5") Integer pageSize
			) {
		
		return cartService.filterAndPaginateCart(username, fullname, fromDate, toDate, sortOrder, pageNumber, pageSize);
	}
}
