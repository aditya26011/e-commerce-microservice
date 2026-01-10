package com.codingshuttle.ecommerce.order_service.service;

import com.codingshuttle.ecommerce.order_service.clients.InventoryOpenFiegnClient;
import com.codingshuttle.ecommerce.order_service.dto.OrderRequestDto;
import com.codingshuttle.ecommerce.order_service.entity.OrderItems;
import com.codingshuttle.ecommerce.order_service.entity.OrderStatus;
import com.codingshuttle.ecommerce.order_service.entity.Orders;
import com.codingshuttle.ecommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final InventoryOpenFiegnClient inventoryOpenFiegnClient;

    public List<OrderRequestDto> getAllOrders() {
        log.info("Fetching all orders");
        List<Orders> orders = orderRepository.findAll();
        return orders.stream().map(order -> modelMapper.map(order, OrderRequestDto.class)).toList();
    }

    public OrderRequestDto getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);
        Orders order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderRequestDto.class);
    }

//@Retry(name = "inventoryRetry",fallbackMethod = "createOrderFallBack")
    @CircuitBreaker(name = "inventoryCircuitBreaker",fallbackMethod = "createOrderFallBack")
@RateLimiter(name = "inventoryRateLimiter",fallbackMethod = "createOrderFallBack")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Calling the createOrder method");
        Double totalPrice = inventoryOpenFiegnClient.reduceStocks(orderRequestDto);

        Orders orders = modelMapper.map(orderRequestDto, Orders.class);
        for(OrderItems orderItem: orders.getItems()) {
            orderItem.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders savedOrder = orderRepository.save(orders);

        return modelMapper.map(savedOrder, OrderRequestDto.class);
    }

    public OrderRequestDto createOrderFallBack(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("FallBack occured due to:{}",throwable.getMessage());
        return new OrderRequestDto();
    }
    }
