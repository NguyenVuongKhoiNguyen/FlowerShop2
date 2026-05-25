package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.OrderDetail;
import com.poly.models.entities.Product;
import com.poly.models.repositories.ProductRepository;
import com.poly.models.requests.OrderDetailRequest;
import com.poly.models.responses.OrderDetailResponse;

import jakarta.persistence.EntityNotFoundException;

@Component
@Mapper(componentModel = "spring")
public abstract class OrderDetailMapper {
	
	@Autowired
	protected ProductRepository productRepo;
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "order", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "price", ignore = true)
	@Mapping(target = "subtotal", ignore = true)
	abstract OrderDetail toEntity(OrderDetailRequest request);
	
	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	@Mapping(source = "product.image", target = "productImage")
	abstract OrderDetailResponse toResonse(OrderDetail orderDetail);
	
	abstract List<OrderDetailResponse> toResponseList(List<OrderDetail> orderDetails);

	abstract List<OrderDetail> toEntityList(List<OrderDetailRequest> orderDetailRequests);
	
	@AfterMapping
	protected void fillOrderDetailEmptyFields(OrderDetailRequest request, @MappingTarget OrderDetail orderDetail) {
		Product product = productRepo.findById(request.getProductId())
				.orElseThrow(() -> new EntityNotFoundException("Product not found with Id: " + request.getProductId()));
		orderDetail.setProduct(product);
		orderDetail.setPrice(product.getRetailPrice());
		orderDetail.setSubtotal(product.getRetailPrice() * request.getQuantity());
	}
}
