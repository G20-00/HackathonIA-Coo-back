package com.coomeva.hackathon.controller;

import com.coomeva.hackathon.dto.CreateOrderRequest;
import com.coomeva.hackathon.dto.OrderItemRequest;
import com.coomeva.hackathon.dto.OrderResponse;
import com.coomeva.hackathon.entity.*;
import com.coomeva.hackathon.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController orderController;

    private User user;
    private Order order;
    private CreateOrderRequest createOrderRequest;
    private Service service;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.UserRole.USER);

        service = new Service();
        service.setId(1L);
        service.setName("Test Service");
        service.setPrice(new BigDecimal("50.00"));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setService(service);
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("50.00"));
        orderItem.setSubtotal(new BigDecimal("50.00"));

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("50.00"));
        order.setItems(new ArrayList<>(List.of(orderItem)));
        orderItem.setOrder(order);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setServiceId(1L);
        itemRequest.setQuantity(1);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setItems(Arrays.asList(itemRequest));
        createOrderRequest.setNotes("Test order");
    }

    @Test
    void getAllOrders_Success() {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order));

        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ORD-12345678", response.getBody().get(0).getOrderNumber());

        verify(orderService).getAllOrders();
    }

    @Test
    void getMyOrders_Success() {
        when(authentication.getName()).thenReturn("test@example.com");
        when(orderService.getOrdersByUser(anyString())).thenReturn(Arrays.asList(order));

        ResponseEntity<List<OrderResponse>> response = orderController.getMyOrders(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ORD-12345678", response.getBody().get(0).getOrderNumber());

        verify(authentication).getName();
        verify(orderService).getOrdersByUser("test@example.com");
    }

    @Test
    void getOrderById_Success() {
        when(orderService.getOrderById(anyLong())).thenReturn(order);

        ResponseEntity<OrderResponse> response = orderController.getOrderById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ORD-12345678", response.getBody().getOrderNumber());
        assertEquals("PENDING", response.getBody().getStatus());

        verify(orderService).getOrderById(1L);
    }

    @Test
    void getOrderById_NotFound() {
        when(orderService.getOrderById(anyLong()))
                .thenThrow(new RuntimeException("Order not found with id: 1"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.getOrderById(1L);
        });

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderService).getOrderById(1L);
    }

    @Test
    void createOrder_Success() {
        when(authentication.getName()).thenReturn("test@example.com");
        when(orderService.createOrder(anyString(), any(CreateOrderRequest.class))).thenReturn(order);

        ResponseEntity<OrderResponse> response = orderController.createOrder(authentication, createOrderRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ORD-12345678", response.getBody().getOrderNumber());
        assertEquals("PENDING", response.getBody().getStatus());

        verify(authentication).getName();
        verify(orderService).createOrder(eq("test@example.com"), any(CreateOrderRequest.class));
    }

    @Test
    void createOrder_EmptyItems() {
        CreateOrderRequest emptyRequest = new CreateOrderRequest();
        emptyRequest.setItems(new ArrayList<>());

        when(authentication.getName()).thenReturn("test@example.com");
        when(orderService.createOrder(anyString(), any(CreateOrderRequest.class)))
                .thenThrow(new RuntimeException("Order must have at least one item"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.createOrder(authentication, emptyRequest);
        });

        assertEquals("Order must have at least one item", exception.getMessage());
        verify(authentication).getName();
        verify(orderService).createOrder(eq("test@example.com"), any(CreateOrderRequest.class));
    }

    @Test
    void updateOrderStatus_Success() {
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setOrderNumber("ORD-12345678");
        updatedOrder.setStatus(Order.OrderStatus.COMPLETED);

        when(orderService.updateOrderStatus(anyLong(), any(Order.OrderStatus.class)))
                .thenReturn(updatedOrder);

        ResponseEntity<Order> response = orderController.updateOrderStatus(1L, Order.OrderStatus.COMPLETED);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Order.OrderStatus.COMPLETED, response.getBody().getStatus());

        verify(orderService).updateOrderStatus(1L, Order.OrderStatus.COMPLETED);
    }

    @Test
    void updateOrderStatus_OrderNotFound() {
        when(orderService.updateOrderStatus(anyLong(), any(Order.OrderStatus.class)))
                .thenThrow(new RuntimeException("Order not found with id: 1"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.updateOrderStatus(1L, Order.OrderStatus.COMPLETED);
        });

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderService).updateOrderStatus(1L, Order.OrderStatus.COMPLETED);
    }
}
