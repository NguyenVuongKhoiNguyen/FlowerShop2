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

import com.poly.models.entities.Order;
import com.poly.models.mappers.OrderMapper;
import com.poly.models.repositories.OrderRepository;
import com.poly.models.requests.OrderRequest;
import com.poly.models.responses.OrderResponse;
import com.poly.models.responses.PageResponse;
import com.poly.models.services.OrderService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

	private final OrderMapper orderMapper;
	
    private final OrderRepository orderRepo;

    @Override
    @Transactional
    @CachePut(value = "orderList", key = "#result.id")
    @CacheEvict(value = "orderPages", allEntries = true)
    public OrderResponse create(OrderRequest resquest) {
        Order order = orderMapper.toEntity(resquest);
        Order saved = orderRepo.save(order);
        return orderMapper.toResponse(saved);
    }
    
    @Override
    @Transactional
    @CachePut(value = "orderList", key = "#id")
    @CacheEvict(value = "orderPages", allEntries = true)
    public OrderResponse update(Long id, OrderRequest request) {
        if (!orderRepo.existsById(id)) {
        	throw new EntityNotFoundException("Order not found with id: "+ id);
        }
        Order order = orderMapper.toEntity(request);
        order.setId(id);
        Order saved = orderRepo.save(order);
        return orderMapper.toResponse(saved);
    }
    
    @Override
    @Transactional
    @Caching(evict = {
    	@CacheEvict(value = "orderList", key = "#id"),
    	@CacheEvict(value = "orderPages", allEntries = true)
    })
    public void delete(Long id) {
        Order exist = orderRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Order not found with Id" + id));
        orderRepo.delete(exist);
    }

    @Override
    @Cacheable(value = "orderList", key = "#id")
    public OrderResponse findById(Long id) {
        Order exist = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with Id: " + id));
        return orderMapper.toResponse(exist);
    }

    @Override
    @Cacheable(value = "orderPages", key = "#username + '_' + #fullname + '_' + #fromDate + '_' + #toDate + '_' + #sortOrder + '_' + #pageNumber + '_' + #pageSize")
    public PageResponse<OrderResponse> filterAndPaginateOrders(
            String username,
            String fullname,
            LocalDate fromDate,
            LocalDate toDate,
            String sortOrder,
            Integer pageNumber,
            Integer pageSize) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC")
        		? Sort.by("id").ascending()
        		: Sort.by("id").descending();
    	Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
    	Page<Order> page = orderRepo.filterOrders(username, fullname, fromDate, toDate, pageable);
    	List<OrderResponse> responses = orderMapper.toResponseList(page.getContent());
        return new PageResponse<>(page, responses);
    }
}
