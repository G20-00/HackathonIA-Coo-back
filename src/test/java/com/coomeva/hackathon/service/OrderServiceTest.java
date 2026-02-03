package com.coomeva.hackathon.service;

import com.coomeva.hackathon.dto.CreateOrderRequest;
import com.coomeva.hackathon.dto.OrderItemRequest;
import com.coomeva.hackathon.entity.*;
import com.coomeva.hackathon.repository.OrderRepository;
import com.coomeva.hackathon.repository.ServiceRepository;
import com.coomeva.hackathon.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Service service1;
    private Service service2;
    private Order order;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.UserRole.USER);

        Category category = new Category();
        category.setId(1L);
        category.setName("Salud");

        service1 = new Service();
        service1.setId(1L);
        service1.setName("Consulta Médica");
        service1.setDescription("Consulta general");
        service1.setPrice(new BigDecimal("50.00"));
        service1.setAvailable(true);
        service1.setType(Service.ServiceType.SALUD);
        service1.setCategory(category);

        service2 = new Service();
        service2.setId(2L);
        service2.setName("Laboratorio");
        service2.setDescription("Examen de laboratorio");
        service2.setPrice(new BigDecimal("30.00"));
        service2.setAvailable(true);
        service2.setType(Service.ServiceType.SALUD);
        service2.setCategory(category);

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("80.00"));

        OrderItemRequest itemRequest1 = new OrderItemRequest();
        itemRequest1.setServiceId(1L);
        itemRequest1.setQuantity(1);

        OrderItemRequest itemRequest2 = new OrderItemRequest();
        itemRequest2.setServiceId(2L);
        itemRequest2.setQuantity(1);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setItems(Arrays.asList(itemRequest1, itemRequest2));
        createOrderRequest.setNotes("Test order");
    }

    @Test
    void getAllOrders_Success() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ORD-12345678", result.getOrderNumber());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrdersByUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(order));

        List<Order> result = orderService.getOrdersByUser("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByEmail("test@example.com");
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    void getOrdersByUser_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrdersByUser("test@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(orderRepository, never()).findByUserId(anyLong());
    }

    @Test
    void createOrder_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service1));
        when(serviceRepository.findById(2L)).thenReturn(Optional.of(service2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        Order result = orderService.createOrder("test@example.com", createOrderRequest);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("80.00"), result.getTotalAmount());
        assertEquals(2, result.getItems().size());
        assertEquals("Test order", result.getNotes());

        verify(userRepository).findByEmail("test@example.com");
        verify(serviceRepository).findById(1L);
        verify(serviceRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_EmptyItems() {
        CreateOrderRequest emptyRequest = new CreateOrderRequest();
        emptyRequest.setItems(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder("test@example.com", emptyRequest);
        });

        assertEquals("Order must have at least one item", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_NullRequest() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder("test@example.com", null);
        });

        assertEquals("Order must have at least one item", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder("test@example.com", createOrderRequest);
        });

        assertEquals("User not found with email: test@example.com", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ServiceNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder("test@example.com", createOrderRequest);
        });

        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(serviceRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ServiceNotAvailable() {
        service1.setAvailable(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder("test@example.com", createOrderRequest);
        });

        assertEquals("Service not available: Consulta Médica", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(serviceRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_NullServiceId() {
        OrderItemRequest itemWithNullId = new OrderItemRequest();
        itemWithNullId.setServiceId(null);

        CreateOrderRequest requestWithNullId = new CreateOrderRequest();
        requestWithNullId.setItems(Collections.singletonList(itemWithNullId));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder("test@example.com", requestWithNullId);
        });

        assertEquals("Service ID cannot be null", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L, Order.OrderStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.COMPLETED, result.getStatus());
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, Order.OrderStatus.COMPLETED);
        });

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
