package com.poly.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.requests.CategoryRequest;
import com.poly.models.responses.CategoryResponse;
import com.poly.models.services.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard/category")
public class DashboardCategoryController {
	
	private final CategoryService categoryService;
	
	@PostMapping
	public CategoryResponse create(@RequestBody CategoryRequest request) {
		return categoryService.create(request);
	}
	
	@PutMapping("{id}")
	public CategoryResponse update(@PathVariable Integer id, @RequestBody CategoryRequest request) {
		return categoryService.update(id, request);
	}
	
	@DeleteMapping("{id}")
	public void delete(Integer id) {
		categoryService.delete(id);
	}
}
