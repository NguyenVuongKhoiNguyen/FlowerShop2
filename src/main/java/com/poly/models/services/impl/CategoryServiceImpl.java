package com.poly.models.services.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.models.entities.Category;
import com.poly.models.repositories.CategoryRepository;
import com.poly.models.requests.CategoryRequest;
import com.poly.models.responses.CategoryResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.CategoryService;
import com.poly.models.mappers.CategoryMappper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final CategoryMappper categoryMapper;
    
    @Override
    @Transactional
    @CachePut(value = "categoryList", key = "#result.id")
    @CacheEvict(value = "categoryPages", allEntries = true)
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepo.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "categoryList", key = "#id")
    public CategoryResponse update(Integer id, CategoryRequest request) {
        if (!categoryRepo.existsById(id))
        	throw new EntityNotFoundException("Category not found with Id: " + id);
        Category category = categoryMapper.toEntity(request);
        category.setId(id);
        Category saved = categoryRepo.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
		@CacheEvict(value = "categoryList", key = "#id"),   // remove this product's cache
        @CacheEvict(value = "categoryPages", allEntries = true)
    })
    public void delete(Integer id) {
        Category exist = categoryRepo.findById(id)
        		.orElseThrow(() -> new EntityNotFoundException("Category not found with Id " + id));
    	categoryRepo.delete(exist);
    }

    @Override
    @Cacheable(value = "categoryList", key = "#id")
    public CategoryResponse findById(Integer id) {
        Category exist = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with Id: " + id));
        return categoryMapper.toResponse(exist);
    }

    @Override
    @Cacheable(value = "categories", key = "selection")
    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepo.findAll();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Cacheable(value = "categoryPages", key = "#keyword + '_' + #sortOrder + '_' + #pageNumber + '_' + #pageSize")
    public PageResponse<CategoryResponse> filterAndPaginateCategories(
            String keyword,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize) {
    	Sort sort = sortOrder.equalsIgnoreCase("ASC") 
    			? Sort.by("id").ascending()
    			: Sort.by("id").descending();
    	Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    	Page<Category> page = categoryRepo.filterCategories(keyword, pageable);
    	List<CategoryResponse> responses = categoryMapper.toResponseList(page.getContent());
        return new PageResponse<>(page, responses);
    }

}