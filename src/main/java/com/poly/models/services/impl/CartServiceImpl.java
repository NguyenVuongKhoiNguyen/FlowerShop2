package com.poly.models.services.impl;

import java.time.LocalDate;
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

import com.poly.models.entities.Cart;
import com.poly.models.mappers.CartMapper;
import com.poly.models.repositories.CartRepository;
import com.poly.models.requests.CartRequest;
import com.poly.models.responses.CartResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.CartService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;                    
    private final CartMapper cartMapper;
    
    @Override
    @Transactional
    @CacheEvict(value = "carts", allEntries = true)
    public CartResponse create(CartRequest request) {
        Cart cart = cartMapper.toEntity(request);
        Cart saved = cartRepo.save(cart);
        return cartMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "cart", key = "#id")
    @CacheEvict(value = "carts", allEntries = true)
    public CartResponse update(Long id, CartRequest request) {
    	if (!cartRepo.existsById(id)) {
    		throw new EntityNotFoundException("Cart not found with id: "+ id);
    	}
    	Cart cart = cartMapper.toEntity(request);
    	cart.setId(id);
        Cart saved = cartRepo.save(cart);
        return cartMapper.toResponse(saved);
    }
    
    @Override
    @Transactional
    @Caching(evict = {
    	@CacheEvict(value = "cart", key = "#id"),
    	@CacheEvict(value = "carts", allEntries = true)
    })
    public void delete(Long id) {
        Cart exist = cartRepo.findById(id)
        		.orElseThrow(() -> new EntityNotFoundException("Cart not found with Id: " + id));
        cartRepo.delete(exist);
    }

    @Override
    @Cacheable(value = "cart", key = "#id")
    public CartResponse findById(Long id) {
        Cart cart = cartRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with Id: " + id)); 
        return cartMapper.toResponse(cart);
    }

	@Override
	@Cacheable(value = "carts", key = "{#username, #fullname, #fromDate, #toDate, #sortOrder, #pageNumber, #pageSize}")
	public PageResponse<CartResponse> filterAndPaginateCart(String username, String fullname, LocalDate fromDate,
			LocalDate toDate, String sortOrder, Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		Sort sort = sortOrder.equalsIgnoreCase("ASC")
				? Sort.by("id").ascending()
				: Sort.by("id").descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Cart> page = cartRepo.filterCarts(username, fullname, fromDate, toDate, pageable);
		List<CartResponse> responses = cartMapper.toResponseList(page.getContent());
		return new PageResponse<>(page, responses);
	}
}
