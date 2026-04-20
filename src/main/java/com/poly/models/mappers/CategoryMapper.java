package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poly.models.entities.Category;
import com.poly.models.requests.CategoryRequest;
import com.poly.models.responses.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)          
    @Mapping(target = "products", ignore = true)    
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);
    
    List<CategoryResponse> toResponseList(List<Category> category);
}
