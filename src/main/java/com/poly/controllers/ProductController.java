package com.poly.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.entities.Product;
import com.poly.models.mappers.ProductMapper;
import com.poly.models.responses.ProductResponse;
import com.poly.models.services.ProductService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
	
	private final ProductMapper pMapper;
	private final ProductService pService;
	
	@GetMapping("/{id}")
	public ProductResponse getById(@PathVariable Integer id) {
		Product p = pService.findById(id);
		return pMapper.toResponse(p);
	}
	
	@GetMapping
	public List<ProductResponse> getAllThenFilterAndPaginate(
				@RequestParam(required = false) Double minPrice,
				@RequestParam(required = false) Double maxPrice,
				@RequestParam(required = false) Integer categoryId,
				@RequestParam(required = false) String productName,
				@RequestParam(required = false) String sortOrderByPriceOrSales,
				@RequestParam(defaultValue = "true") Boolean available,
				@RequestParam(defaultValue = "1") Integer page,
				@RequestParam(defaultValue = "8") Integer pageSize
			) {
		
		if (categoryId != null && categoryId == 0) categoryId = null;
	    
		
	    if (minPrice != null && maxPrice == null) {
	    	maxPrice = minPrice;
	    	minPrice = 0.0;
	    }
	    
	    if (minPrice == null && maxPrice != null) 
	    	minPrice = 0.0;
	    
	    if (minPrice != null && maxPrice != null) {
	    	if (maxPrice < minPrice) {
	    		double temp = minPrice;
	    		minPrice = maxPrice;
	    		maxPrice = temp;
	    	}
	    }
	
		
	    List<Product> products = pService.filteredPaginated(minPrice, maxPrice, categoryId, productName, available, sortOrderByPriceOrSales, page, pageSize);
	    
		return pMapper.toResponseList(products);
	}
	
	@GetMapping("/total-pages")
	public Integer getTotalPages(
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false) String productName,
			@RequestParam(defaultValue = "true") Boolean available,
			@RequestParam(defaultValue = "8") Integer pageSize
			) {
		
		long totalRows = pService.countFiltered(minPrice, maxPrice, categoryId, productName, available);
	    int totalPages = (int) Math.ceil((double) totalRows / pageSize);
		
		return totalPages;
	}
	
	@GetMapping("/top-sales")
	public List<ProductResponse> getTop8ByAvailableTrueOrderBySalesDESC() {
		List<Product> products = pService.findTop8ByAvailableTrueOrderBySalesDESC();
		return pMapper.toResponseList(products);
	}
	
	@GetMapping("/random")
	public List<ProductResponse> getTop8AvailableRandom() {
		List<Product> products = pService.findTop8RandomAvailableProducts();
		return pMapper.toResponseList(products);
	}
}
