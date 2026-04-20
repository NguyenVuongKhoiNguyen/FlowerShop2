package com.poly.models.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.models.entities.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	List<Cart>findByAccountUsernameOrderByCreateDateDesc(String username);
}
