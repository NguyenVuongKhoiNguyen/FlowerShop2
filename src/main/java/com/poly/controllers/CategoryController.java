package com.poly.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.entities.Category;
import com.poly.models.mappers.CategoryMapper;
import com.poly.models.responses.CategoryResponse;
import com.poly.models.services.CategoryService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryMapper cMapper;
	private final CategoryService cService;
	
	@GetMapping("/{id}")
	public CategoryResponse getById(@PathVariable Integer id) {
		Category c = cService.findById(id);
		return cMapper.toResponse(c);
	}
	
	@GetMapping
	public List<CategoryResponse> getAllThenFilterAndPaginate(
				@RequestParam(required = false) String keyword,
				@RequestParam(required = false) String sortOrder,
				@RequestParam(defaultValue = "1") Integer page,
				@RequestParam(defaultValue = "8") Integer pageSize
			) {
		
		if (page == null || pageSize == null)
			return cMapper.toResponseList(cService.findAll());
		
		List<Category> categories = cService.filteredPaginatedCategories(keyword, sortOrder, page, pageSize);
		return cMapper.toResponseList(categories);
	}
	
	@GetMapping("/total-pages")
	public Integer getTotalPages(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String sortOrder,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "8") Integer pageSize
			) {
		long totalRows = cService.countCategories(keyword);
	    int totalPages = (int) Math.ceil((double) totalRows / pageSize);
		
	    return totalPages;
	}
}
