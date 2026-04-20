package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.poly.models.entities.Cart;
import com.poly.models.entities.Item;
import com.poly.models.requests.CartRequest;
import com.poly.models.responses.CartResponse;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface CartMapper {
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "account", ignore = true) 
    @Mapping(target = "items", ignore = true)  
    @Mapping(target = "total", ignore = true)
	Cart toEntity(CartRequest request);
	
    @Mapping(target = "username", source = "account.username")
	CartResponse toResponse(Cart cart);
    
    List<CartResponse> toResponseList(List<Cart> carts);
    
    @AfterMapping
    default void linkItems(@MappingTarget Cart cart) {
        if (cart.getItems() != null) {
            for (Item item : cart.getItems()) {
                item.setCart(cart);
            }
        }
    }
}
