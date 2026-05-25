package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Category;
import com.poly.models.requests.CategoryRequest;
import com.poly.models.responses.CategoryResponse;

@Component
@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public abstract class CategoryMappper {
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "products", ignore = true)
	public abstract Category toEntity(CategoryRequest request);
	public abstract CategoryResponse toResponse(Category category);
	public abstract List<CategoryResponse> toResponseList(List<Category> categories);
}
