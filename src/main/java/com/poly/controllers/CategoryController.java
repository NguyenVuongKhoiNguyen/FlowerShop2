package com.poly.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.responses.CategoryResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
	
	private final CategoryService categoryService;
	
	public PageResponse<CategoryResponse> filterCategories(
			@RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "DESC") String sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize
			) {
		return categoryService.filterAndPaginateCategories(keyword, sortOrder, pageNumber, pageSize);
	}
	
	@GetMapping("/all")
	public List<CategoryResponse> getAll() {
		return categoryService.findAll();
	}
}
