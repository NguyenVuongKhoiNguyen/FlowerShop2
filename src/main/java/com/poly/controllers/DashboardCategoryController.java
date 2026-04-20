package com.poly.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poly.models.entities.Category;
import com.poly.models.mappers.CategoryMapper;
import com.poly.models.requests.CategoryRequest;
import com.poly.models.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RequestMapping("/dashboard/categories")
@RestController
@RequiredArgsConstructor
public class DashboardCategoryController {
	
	private final CategoryMapper cMapper;
	private final CategoryService cService;
	
	// CREATE
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody CategoryRequest request) {
		try {
			Category c = cMapper.toEntity(request);
			Category saved = cService.create(c);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(cMapper.toResponse(saved));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Create failed: " + e.getMessage());
		}
	}
	
	// UPDATE
	@PutMapping("/{id}")
	public ResponseEntity<?> update(
			@PathVariable Integer id,
			@Valid @RequestBody CategoryRequest request) {
		try {
			Category existing = cService.findById(id);

			// update fields (adjust if you have more)
			existing.setName(request.getName());

			Category updated = cService.update(id, existing);

			return ResponseEntity.ok(cMapper.toResponse(updated));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Category not found: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Update failed: " + e.getMessage());
		}
	}
	
	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		try {
			cService.deleteById(id);
			return ResponseEntity.noContent().build(); // 204
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Category not found: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Delete failed: " + e.getMessage());
		}
	}
}
