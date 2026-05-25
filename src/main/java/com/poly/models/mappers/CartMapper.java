package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Account;
import com.poly.models.entities.Cart;
import com.poly.models.repositories.AccountRepository;
import com.poly.models.requests.CartRequest;
import com.poly.models.responses.CartResponse;

import jakarta.persistence.EntityNotFoundException;

@Component
@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public abstract class CartMapper {
	
	@Autowired
	protected AccountRepository accountRepo;
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "account", ignore = true)
	@Mapping(target = "total", ignore = true)
	@Mapping(target = "fullname", ignore = true)
	@Mapping(source = "itemRequests", target = "items")
	public abstract Cart toEntity(CartRequest request);
	
	@Mapping(source = "account.username", target = "username")
	@Mapping(source = "items", target = "itemResponses")
	public abstract CartResponse toResponse(Cart cart);
	
	public abstract List<CartResponse> toResponseList(List<Cart> carts);
	
	protected void fillCartEmptyFields(CartRequest request, @MappingTarget Cart cart) {
		Account account = accountRepo.findById(request.getUsername())
				.orElseThrow(() -> new EntityNotFoundException("Account not found with username: " + request.getUsername()));
		cart.setFullname(account.getFullname());
		cart.setAccount(account);
		if (cart.getItems() != null && !cart.getItems().isEmpty()) {
			cart.calculateTotal();
		}
	}
}
