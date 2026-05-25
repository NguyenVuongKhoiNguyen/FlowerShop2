package com.poly.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.responses.PageResponse;
import com.poly.models.responses.ProductResponse;
import com.poly.models.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
	
	private final ProductService productService;
	
	@GetMapping("{id}")
	public ProductResponse getProductById(@PathVariable Integer id) {
		
		return productService.findById(id);
	}
	
	@GetMapping
	public PageResponse<ProductResponse> filterProducts(
			@RequestParam(required = false) Double minPrice, 
			@RequestParam(required = false) Double maxPrice, 
			@RequestParam(required = false) Integer categoryId, 
			@RequestParam(required = false) String productName, 
			@RequestParam(required = false) Boolean available, 
			@RequestParam(defaultValue = "DESC") String sortOrder, 
			@RequestParam(defaultValue = "1") Integer page, 
			@RequestParam(defaultValue = "5") Integer pageSize
			) {
		
		return productService.filterAndPaginateProducts(minPrice, maxPrice, categoryId, productName, available, sortOrder, page, pageSize);
	}
}
