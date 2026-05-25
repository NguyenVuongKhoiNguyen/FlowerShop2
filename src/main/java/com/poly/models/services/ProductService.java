package com.poly.models.services;

import java.util.List;

import com.poly.models.requests.ProductRequest;
import com.poly.models.responses.PageResponse;
import com.poly.models.responses.ProductResponse;

public interface ProductService {
	ProductResponse create(ProductRequest request);
	ProductResponse update(Integer id, ProductRequest request);
	void delete(Integer id);
	ProductResponse findById(Integer id);
	PageResponse<ProductResponse> filterAndPaginateProducts(
			Double minPrice, 
			Double maxPrice, 
			Integer categoryId, 
			String productName, 
			Boolean available, 
			String sortOrder, 
			Integer pageNumber, 
			Integer pageSize);
	List<ProductResponse> find8MostSalesProduct();
	List<ProductResponse> find8RandomProduct();
}
