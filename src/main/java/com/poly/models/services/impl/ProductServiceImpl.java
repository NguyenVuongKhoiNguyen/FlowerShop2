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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.models.entities.Product;
import com.poly.models.mappers.ProductMapper;
import com.poly.models.repositories.ProductRepository;
import com.poly.models.requests.ProductRequest;
import com.poly.models.responses.PageResponse;
import com.poly.models.responses.ProductResponse;
import com.poly.models.services.ProductService;
import com.poly.utils.ImageUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepo;
	
	private final ProductMapper productMapper;
	
	@Override
	@Transactional
	@CachePut(value = "productList", key = "#result.id")
    @CacheEvict(value = "productPages", allEntries = true) 
	public ProductResponse create(ProductRequest request) {
	    Product product = productMapper.toEntity(request);
	    Product saved = productRepo.save(product);
	    return productMapper.toResponse(saved);
	}
	
	@Override
	@Transactional
    @CachePut(value = "productList", key = "#id")         
    @CacheEvict(value = "productPages", allEntries = true) 
	public ProductResponse update(Integer id, ProductRequest request) {
		if (!productRepo.existsById(id))
				throw new UsernameNotFoundException("Product not found with Id: " + id);
		Product product = productMapper.toEntity(request);
		Product saved = productRepo.save(product);
		return productMapper.toResponse(saved);
	}
	
	@Override
	@Transactional
	@Caching(evict = {
	        @CacheEvict(value = "productList", key = "#id"),   
	        @CacheEvict(value = "productPages", allEntries = true) 
	    })
	public void delete(Integer id) {
		Product exist = productRepo.findById(id).
				orElseThrow(() -> new EntityNotFoundException("Product not found with Id: " + id));
		ImageUtil.deleteImage(exist.getImage());
		productRepo.delete(exist);
	}
	
	@Override
    @Cacheable(value = "productList", key = "#id")         
	public ProductResponse findById(Integer id) {
		Product exist = productRepo.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("Product not found with Id: " + id));
		return productMapper.toResponse(exist);
	}
	
	@Override
    @Cacheable(value = "productPages", key = "#minPrice + '_' + #maxPrice + '_' + #categoryId + '_' + #productName + '_' + #available + '_' + #sortOrder + '_' + #pageNumber + '_' + #pageSize")
	public PageResponse<ProductResponse> filterAndPaginateProducts(
			Double minPrice,
			Double maxPrice,
			Integer categoryId,
			String productName, 
			Boolean available,
			String sortOrder,
			Integer pageNumber,
			Integer pageSize) {
        
		Sort sort = null;
		if (sortOrder == null || sortOrder.equalsIgnoreCase("DESC"))
			sort = Sort.by("id").descending();
		if (sortOrder.equalsIgnoreCase("ASC"))
			sort = Sort.by("id").ascending();
		if (sortOrder.equalsIgnoreCase("PRICE-ASC"))
			sort = Sort.by("price").ascending();
		if (sortOrder.equalsIgnoreCase("PRICE-DESC"))
			sort = Sort.by("price").descending();
		if (sortOrder.equalsIgnoreCase("SALES-ASC"))
			sort = Sort.by("sales").ascending();
		if (sortOrder.equalsIgnoreCase("SALES-DESC"))
			sort = Sort.by("sales").descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Product> page = productRepo.filterProducts(minPrice, maxPrice, categoryId, productName, available, pageable);
		List<ProductResponse> responses = productMapper.toResponseList(page.getContent());
        return new PageResponse<>(page, responses);
    }
	
	@Override
	@Cacheable(value ="top8Sales", key = "top8MostSale")
	public List<ProductResponse> find8MostSalesProduct() {
		List<Product> products =  productRepo.findTop8ByAvailableTrueOrderBySalesDesc();
		return productMapper.toResponseList(products);
	}
	
	@Override
	public List<ProductResponse> find8RandomProduct() {
		List<Product> products = productRepo.findTop8RandomAvailableProducts();
		return productMapper.toResponseList(products);
	}
	
	/**
	 * Cacheable loop through cache first 
	 * if found then return that values, no method run
	 * if not found then run method, add values into catch
	 * 
	 * CacheEvict delete entries in cache
	 * 
	 * CachePut update entries in cache
	 * 
	 * value = "product" stores a product list
	 * value = "products" stores multiple product lists
	 * need two seperate memory spaces, one for create update and delete
	 * other is for get 
	 * 
	 * TTL (Time to Live) evict entries after the time expired
	 * LRU (Least Recently Use) evict entry when the allocated memory is full
	 */
}
