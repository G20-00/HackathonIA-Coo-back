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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Service service;
    private Order order;
    private CreateOrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");

        Category category = new Category();
        category.setId(1L);
        category.setName("Salud");

        service = new Service();
        service.setId(1L);
        service.setName("Plan Médico");
        service.setPrice(new BigDecimal("100000"));
        service.setCategory(category);
        service.setAvailable(true);

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-001");
        order.setUser(user);
        order.setTotalAmount(new BigDecimal("100000"));
        order.setStatus(Order.OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setService(service);
        item.setQuantity(1);
        item.setPrice(new BigDecimal("100000"));
        item.setSubtotal(new BigDecimal("100000"));
        order.setItems(Arrays.asList(item));

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setServiceId(1L);
        itemRequest.setQuantity(1);

        orderRequest = new CreateOrderRequest();
        orderRequest.setItems(Arrays.asList(itemRequest));
        orderRequest.setNotes("Test notes");
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Given
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertThat(result).hasSize(1);
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_WhenExists_ShouldReturnOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When
        Order result = orderService.getOrderById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_WhenNotExists_ShouldThrowException() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void createOrder_WithValidData_ShouldCreateOrder() {
        // Given
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = orderService.createOrder("test@test.com", orderRequest);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findByEmail("test@test.com");
        verify(serviceRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_WithInvalidUser_ShouldThrowException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> orderService.createOrder("invalid@test.com", orderRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getOrdersByUser_ShouldReturnUserOrders() {
        // Given
        List<Order> orders = Arrays.asList(order);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(1L)).thenReturn(orders);

        // When
        List<Order> result = orderService.getOrdersByUser("test@test.com");

        // Then
        assertThat(result).hasSize(1);
        verify(orderRepository).findByUserId(1L);
    }

    @Test
    void updateOrderStatus_WithValidStatus_ShouldUpdateOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = orderService.updateOrderStatus(1L, Order.OrderStatus.COMPLETED);

        // Then
        assertThat(result).isNotNull();
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }
}
