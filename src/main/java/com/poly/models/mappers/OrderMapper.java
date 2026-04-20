package com.poly.models.mappers;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.poly.models.entities.Order;
import com.poly.models.entities.OrderDetail;
import com.poly.models.requests.OrderRequest;
import com.poly.models.responses.OrderResponse;

@Mapper(componentModel = "spring", uses = OrderDetailMapper.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "status", ignore = true)    
    @Mapping(target = "account", ignore = true)    
    @Mapping(target = "orderDetails", ignore = true) 
    @Mapping(target = "total", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(target = "username", source = "account.username")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);

    @AfterMapping
    default void linkOrder(@MappingTarget Order order) {
        if (order.getOrderDetails() != null) {
            for (OrderDetail od : order.getOrderDetails()) {
                od.setOrder(order);
            }
        }
    }
}
