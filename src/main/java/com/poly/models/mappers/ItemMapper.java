package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poly.models.entities.Item;
import com.poly.models.entities.Product;
import com.poly.models.repositories.ProductRepository;
import com.poly.models.requests.ItemRequest;
import com.poly.models.responses.ItemResponse;

import jakarta.persistence.EntityNotFoundException;

@Component
@Mapper(componentModel = "spring")
public abstract class ItemMapper {
	
	@Autowired
	protected ProductRepository productRepo;
	
	@Mapping(target ="id", ignore = true)
	@Mapping(target = "cart", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "subtotal", ignore = true)
	abstract Item toEntity(ItemRequest request);
	
	@Mapping(source = "product.id", target = "productId")
	@Mapping(source = "product.name", target = "productName")
	@Mapping(source = "product.image", target = "productImage")
	@Mapping(target = "price", expression = "java(item.getProduct().getRetailPrice())")
	abstract ItemResponse toResponse(Item item);
	
	abstract List<Item> toEntityList(List<ItemRequest> itemRequests);
	
	abstract List<ItemResponse> toResponseList(List<Item> items);
	
	@AfterMapping
	protected void fillItemEmptyFields(ItemRequest request, @MappingTarget Item item) {
		Product product = productRepo.findById(request.getProductId())
				.orElseThrow(() -> new EntityNotFoundException("Product not found with Id: " + request.getProductId()));
		item.setProduct(product);
		item.setSubtotal(product.getRetailPrice() * request.getQuantity());
	}
	
}
