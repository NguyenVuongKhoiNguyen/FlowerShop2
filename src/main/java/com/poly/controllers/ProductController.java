package com.poly.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

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
			@RequestParam(defaultValue = "") Double minPrice, 
			@RequestParam(defaultValue = "") Double maxPrice, 
			@RequestParam(defaultValue = "") Integer categoryId, 
			@RequestParam(defaultValue = "") String productName, 
			@RequestParam(defaultValue = "true") Boolean available, 
			@RequestParam(defaultValue = "DESC") String sortOrder, 
			@RequestParam(defaultValue = "1") Integer page, 
			@RequestParam(defaultValue = "5") Integer pageSize
			) {
		
		return productService.filterAndPaginateProducts(minPrice, maxPrice, categoryId, productName, available, sortOrder, page, pageSize);
	}

	@GetMapping("/preload")
	public Map<String, PageResponse<ProductResponse>> preloadProducts() {

		Map<String, PageResponse<ProductResponse>> map = new LinkedHashMap<>();

		for (int i = 0; i < 5; i++) {
			String key = "" + "_"  + "" + "_" + "" + "_" + "" + "_" + "true" + "_" + "DESC" + "_" + i + "_" + "5";
			map.put(key, productService.filterAndPaginateProducts(null, null, null, null, true, "DESC", i, 5));
		}
		
		return map;
	}
}
