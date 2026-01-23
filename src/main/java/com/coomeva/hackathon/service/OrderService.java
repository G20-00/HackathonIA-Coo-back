package com.coomeva.hackathon.service;

import com.coomeva.hackathon.dto.CreateOrderRequest;
import com.coomeva.hackathon.dto.OrderItemRequest;
import com.coomeva.hackathon.entity.Order;
import com.coomeva.hackathon.entity.OrderItem;
import com.coomeva.hackathon.entity.Service;
import com.coomeva.hackathon.entity.User;
import com.coomeva.hackathon.repository.OrderRepository;
import com.coomeva.hackathon.repository.ServiceRepository;
import com.coomeva.hackathon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserId(user.getId());
    }

    @Transactional
    public Order createOrder(String userEmail, CreateOrderRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setNotes(request.getNotes());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Service service = serviceRepository.findById(itemRequest.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setService(service);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(service.getPrice());
            
            BigDecimal subtotal = service.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
