package com.poly.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poly.models.requests.ProductRequest;
import com.poly.models.responses.ProductResponse;
import com.poly.models.services.ProductService;
import com.poly.utils.ImageUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard/product")
@RequiredArgsConstructor
public class DashboardProductController {
	
	private final ProductService productService;
	
	@PostMapping
	public ProductResponse create(@RequestParam("file") MultipartFile file, @RequestBody ProductRequest request) {
		
		String image = ImageUtil.saveImage(file);
		request.setImage(image);
		return productService.create(request);
	}
	
	@PutMapping("{id}")
	public ProductResponse update(@RequestParam("file") MultipartFile file, @PathVariable Integer id, @RequestBody ProductRequest request) {
		
		if (file != null && !file.isEmpty()) {
			String image = ImageUtil.saveImage(file);
			request.setImage(image);
			ImageUtil.deleteImage(request.getImage());
		}
		return productService.update(id, request);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable Integer id) {
		
		productService.delete(id);
	}
}
