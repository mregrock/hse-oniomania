package com.example.orderservice.mapper;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public Order toEntity(CreateOrderRequest request) {
    Order order = new Order();
    order.setUserId(request.getUserId());
    order.setDescription(request.getDescription());
    order.setAmount(request.getAmount());
    return order;
  }

  public OrderResponse toResponse(Order order) {
    OrderResponse response = new OrderResponse();
    response.setId(order.getId());
    response.setUserId(order.getUserId());
    response.setDescription(order.getDescription());
    response.setAmount(order.getAmount());
    response.setStatus(order.getStatus());
    return response;
  }
}
