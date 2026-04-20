package com.poly.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poly.models.entities.Item;
import com.poly.models.responses.ItemResponse;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productImage", source = "product.image")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "price", expression = "java(item.product.getRetailPrice())")
    @Mapping(target = "subtotal", expression = "java(item.getSubtotal())")
    ItemResponse toResponse(Item item);
}