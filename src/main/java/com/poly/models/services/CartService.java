package com.poly.models.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.poly.models.entities.Cart;
import com.poly.models.repositories.CartRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepo;                    

    public Cart create(Cart cart) {
        return cartRepo.save(cart);
    }

    public Cart update(Long id, Cart cart) {
        if (!cartRepo.existsById(id)) {
            throw new EntityNotFoundException("Cart not found: " + id);
        }
        cart.setId(id);
        return cartRepo.save(cart);
    }

    public void deleteById(Long id) {
        if (!cartRepo.existsById(id)) {
            throw new EntityNotFoundException("Cart not found: " + id);
        }
        cartRepo.deleteById(id);
    }

    public Cart findById(Long id) {
        return cartRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found: " + id)); // orElseThrow
    }

    public List<Cart> findByUsername(String username) {         // shorter cleaner name
        return cartRepo.findByAccountUsernameOrderByCreateDateDesc(username);
    }
}
