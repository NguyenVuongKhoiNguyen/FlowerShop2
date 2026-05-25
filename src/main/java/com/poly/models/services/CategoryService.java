package com.poly.models.services;

import java.util.List;

import com.poly.models.requests.CategoryRequest;
import com.poly.models.responses.CategoryResponse;
import com.poly.models.responses.PageResponse;

public interface CategoryService {
	CategoryResponse create(CategoryRequest request);
	CategoryResponse update(Integer id, CategoryRequest request);
	void delete(Integer id);
	CategoryResponse findById(Integer id);
	List<CategoryResponse> findAll();
	PageResponse<CategoryResponse> filterAndPaginateCategories(
            String keyword,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize
    );
}
