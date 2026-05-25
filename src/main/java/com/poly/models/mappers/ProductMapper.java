package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Category;
import com.poly.models.entities.Product;
import com.poly.models.repositories.CategoryRepository;
import com.poly.models.requests.ProductRequest;
import com.poly.models.responses.ProductResponse;

import jakarta.persistence.EntityNotFoundException;

@Component
@Mapper(componentModel = "spring")
public abstract class ProductMapper {
	
	@Autowired
	protected CategoryRepository categoryRepo;
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "comments", ignore = true) 
	@Mapping(target = "category", ignore = true) 
	public abstract Product toEntity(ProductRequest request);
	
	@Mapping(target = "categoryId", source = "category.id")
	public abstract ProductResponse toResponse(Product product);
	
	public abstract List<ProductResponse> toResponseList(List<Product> products);
	
	@AfterMapping
	protected void fieldProductEmptyFields(ProductRequest request, @MappingTarget Product product) {
		Category category = categoryRepo.findById(request.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("Category not found with Id: " + request.getCategoryId()));
		product.setCategory(category);
	}
}
