package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.poly.models.entities.Category;
import com.poly.models.entities.Product;
import com.poly.models.requests.ProductRequest;
import com.poly.models.responses.ProductResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
	@Mapping(target = "sales", ignore = true)
	@Mapping(target = "comments", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	Product toEntity(ProductRequest request);
	
	@Mapping(target = "categoryId", source = "category.id")
	ProductResponse toResponse(Product product);
	
	List<ProductResponse> toResponseList(List<Product> products);
	
	@Named("mapCategory")
	default Category mapCategory(Integer categoryId) {
		if (categoryId == null) return null;
		Category category = new Category();
		category.setId(categoryId); //missing category.setName is not a problem
		return category;
	}
}
