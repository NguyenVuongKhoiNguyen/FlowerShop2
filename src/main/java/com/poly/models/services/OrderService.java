package com.poly.models.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poly.models.entities.Order;
import com.poly.models.repositories.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;

    public Order create(Order order) {
        if (order.getId() != null && orderRepo.existsById(order.getId())) {
            throw new IllegalArgumentException("Order already exists: " + order.getId());
        }
        return orderRepo.save(order);
    }

    public Order update(Long id, Order order) {
        if (!orderRepo.existsById(id)) {
            throw new EntityNotFoundException("Order not found: " + id);
        }
        order.setId(id);
        return orderRepo.save(order);
    }

    public void delete(Long id) {
        if (!orderRepo.existsById(id)) {
            throw new EntityNotFoundException("Order not found: " + id);
        }
        orderRepo.deleteById(id);
    }

    public Order findById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    public List<Order> filteredAndPaginated(
            String username,
            String fullname,
            LocalDate fromDate,
            LocalDate toDate,
            Integer page,
            Integer pageSize) {
        return orderRepo.filteredAndPaginated(username, fullname, fromDate, toDate, page, pageSize);
    }

    public long countFilteredOrders(
            String username,
            String fullname,
            LocalDate fromDate,
            LocalDate toDate) {
        return orderRepo.countFilteredOrders(username, fullname, fromDate, toDate);
    }
}
