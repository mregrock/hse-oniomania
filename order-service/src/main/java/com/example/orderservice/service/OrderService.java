package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
  OrderResponse createOrder(CreateOrderRequest request);
  List<OrderResponse> getOrdersByUserId(Long userId);
  OrderResponse getOrderById(Long orderId);
}
