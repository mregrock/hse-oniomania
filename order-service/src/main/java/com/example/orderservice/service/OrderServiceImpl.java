package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderCreatedEvent;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.model.Outbox;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OutboxRepository outboxRepository;
  private final OrderMapper orderMapper;
  private final ObjectMapper objectMapper;

  @Transactional
  @Override
  public OrderResponse createOrder(CreateOrderRequest request) {
    log.info("Creating order for user: {}", request.getUserId());
    Order order = orderMapper.toEntity(request);
    order.setStatus(OrderStatus.NEW);
    order = orderRepository.save(order);
    log.info("Order with id: {} saved with status NEW", order.getId());

    OrderCreatedEvent event = new OrderCreatedEvent(
          UUID.randomUUID(),
          order.getId(),
          order.getUserId(),
          order.getAmount(),
          order.getDescription()
    );
    log.info("OrderCreatedEvent created: {}", event);

    String payload;
    try {
        payload = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to serialize OrderCreatedEvent", e);
    }

    Outbox outboxEvent = new Outbox(
            "Order",
            order.getId().toString(),
            "orders.created",
            payload
    );
    outboxRepository.save(outboxEvent);
    log.info("Outbox event saved for orderId: {}", order.getId());

    return orderMapper.toResponse(order);
  }

  @Override
  public List<OrderResponse> getOrdersByUserId(Long userId) {
    return orderRepository.findByUserId(userId).stream()
          .map(orderMapper::toResponse)
          .collect(Collectors.toList());
  }

  @Override
  public OrderResponse getOrderById(Long orderId) {
    return orderRepository.findById(orderId)
          .map(orderMapper::toResponse)
          .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
  }
}
