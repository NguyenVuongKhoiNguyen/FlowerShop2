package com.poly.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poly.models.entities.OrderDetail;
import com.poly.models.responses.OrderDetailResponse;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", source = "product.image")
    @Mapping(target = "subtotal", expression = "java(orderDetail.getSubtotal())")
    OrderDetailResponse toResponse(OrderDetail orderDetail);
}
