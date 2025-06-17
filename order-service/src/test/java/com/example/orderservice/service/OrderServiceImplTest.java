package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private OutboxRepository outboxRepository;
  @Mock
  private OrderMapper orderMapper;
  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private OrderServiceImpl orderService;

  @Test
  void createOrder_shouldCreateOrderAndOutboxEvent() throws Exception {
    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);
    request.setDescription("Aboba Order");
    request.setAmount(BigDecimal.TEN);

    Order order = new Order(1L, 1L, "Aboba Order", BigDecimal.TEN, OrderStatus.NEW);
    OrderResponse orderResponse = new OrderResponse();

    when(orderMapper.toEntity(any(CreateOrderRequest.class))).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");
    when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

    OrderResponse createdOrder = orderService.createOrder(request);

    assertNotNull(createdOrder);
    assertEquals(orderResponse, createdOrder);
    verify(orderRepository, times(1)).save(any(Order.class));
    verify(outboxRepository, times(1)).save(any());
  }

  @Test
  void getOrdersByUserId_shouldReturnUserOrders() {
    Long userId = 1L;
    Order order = new Order(1L, userId, "Aboba", BigDecimal.ONE, OrderStatus.NEW);
    OrderResponse orderResponse = new OrderResponse();
    
    when(orderRepository.findByUserId(userId)).thenReturn(Collections.singletonList(order));
    when(orderMapper.toResponse(order)).thenReturn(orderResponse);

    List<OrderResponse> orders = orderService.getOrdersByUserId(userId);

    assertFalse(orders.isEmpty());
    assertEquals(1, orders.size());
    assertEquals(orderResponse, orders.get(0));
  }

  @Test
  void getOrderById_shouldReturnOrder_whenExists() {
    Long orderId = 1L;
    Order order = new Order(orderId, 1L, "Aboba", BigDecimal.ONE, OrderStatus.NEW);
    OrderResponse orderResponse = new OrderResponse();

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderMapper.toResponse(order)).thenReturn(orderResponse);

    OrderResponse foundOrder = orderService.getOrderById(orderId);

    assertNotNull(foundOrder);
    assertEquals(orderResponse, foundOrder);
  }

  @Test
  void getOrderById_shouldThrowException_whenNotExists() {
    Long orderId = 1L;
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
  }
}
