package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Account;
import com.poly.models.entities.Order;
import com.poly.models.repositories.AccountRepository;
import com.poly.models.requests.OrderRequest;
import com.poly.models.responses.OrderResponse;

import jakarta.persistence.EntityNotFoundException;

@Component
@Mapper(componentModel = "spring", uses = {OrderDetailMapper.class})
public abstract class OrderMapper {
	
	@Autowired
	protected AccountRepository accountRepo;
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "account", ignore = true)
	@Mapping(target = "total", ignore = true)
	@Mapping(source = "orderDetailRequests", target = "orderDetails")
	public abstract Order toEntity(OrderRequest request);
	
	@Mapping(source = "account.username", target = "username")
	@Mapping(source = "orderDetails", target = "orderDetailResponses")
	public abstract OrderResponse toResponse(Order order);
	
	public abstract List<OrderResponse> toResponseList(List<Order> orders);
		
	@AfterMapping
	protected void fillOrderEmptyFields(OrderRequest request, @MappingTarget Order order) {
		Account account = accountRepo.findById(request.getUsername())
				.orElseThrow(() -> new EntityNotFoundException("Account not found with username: " + request.getUsername()));;
		
		order.setAccount(account);
		if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty())
			order.calculateTotal();
	}
}
