package com.coomeva.hackathon.service;

import com.coomeva.hackathon.dto.PaymentRequest;
import com.coomeva.hackathon.entity.Order;
import com.coomeva.hackathon.entity.Payment;
import com.coomeva.hackathon.entity.User;
import com.coomeva.hackathon.repository.OrderRepository;
import com.coomeva.hackathon.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private Payment payment;
    private PaymentRequest paymentRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD-12345678");
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));

        payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setTransactionId("TXN-12345678");

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
    }

    @Test
    void processPayment_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            savedPayment.setId(1L);
            return savedPayment;
        });

        Payment result = paymentService.processPayment(paymentRequest);

        assertNotNull(result);
        assertEquals(order, result.getOrder());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(Payment.PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("TXN-"));
        assertEquals("Payment processed successfully - SANDBOX MODE", result.getGatewayResponse());
        assertNotNull(result.getCompletedAt());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void processPayment_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPayment_OrderAlreadyHasPayment() {
        order.setPayment(payment);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(paymentRequest);
        });

        assertEquals("Order already has a payment", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPayment_WithDebitCard() {
        paymentRequest.setPaymentMethod(Payment.PaymentMethod.DEBIT_CARD);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            savedPayment.setId(1L);
            return savedPayment;
        });

        Payment result = paymentService.processPayment(paymentRequest);

        assertNotNull(result);
        assertEquals(Payment.PaymentMethod.DEBIT_CARD, result.getPaymentMethod());
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void processPayment_WithPSE() {
        paymentRequest.setPaymentMethod(Payment.PaymentMethod.PSE);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            savedPayment.setId(1L);
            return savedPayment;
        });

        Payment result = paymentService.processPayment(paymentRequest);

        assertNotNull(result);
        assertEquals(Payment.PaymentMethod.PSE, result.getPaymentMethod());
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getPaymentByOrderId_Success() {
        when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentByOrderId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(order, result.getOrder());
        verify(paymentRepository).findByOrderId(1L);
    }

    @Test
    void getPaymentByOrderId_NotFound() {
        when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentByOrderId(1L);
        });

        assertEquals("Payment not found for order", exception.getMessage());
        verify(paymentRepository).findByOrderId(1L);
    }

    @Test
    void getPaymentById_Success() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(order, result.getOrder());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void getPaymentById_NotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentById(1L);
        });

        assertEquals("Payment not found", exception.getMessage());
        verify(paymentRepository).findById(1L);
    }
}
