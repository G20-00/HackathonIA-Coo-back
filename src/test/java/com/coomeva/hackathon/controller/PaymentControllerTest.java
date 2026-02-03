package com.coomeva.hackathon.controller;

import com.coomeva.hackathon.dto.PaymentRequest;
import com.coomeva.hackathon.dto.PaymentResponse;
import com.coomeva.hackathon.entity.Order;
import com.coomeva.hackathon.entity.Payment;
import com.coomeva.hackathon.entity.User;
import com.coomeva.hackathon.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private User user;
    private Order order;
    private Payment payment;
    private PaymentRequest paymentRequest;

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
        payment.setGatewayResponse("Payment processed successfully - SANDBOX MODE");
        payment.setCompletedAt(LocalDateTime.now());

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
    }

    @Test
    void processPayment_Success() {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(payment);

        ResponseEntity<PaymentResponse> response = paymentController.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TXN-12345678", response.getBody().getTransactionId());
        assertEquals(new BigDecimal("100.00"), response.getBody().getAmount());
        assertEquals("COMPLETED", response.getBody().getStatus());
        assertEquals("CREDIT_CARD", response.getBody().getPaymentMethod());

        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void processPayment_OrderNotFound() {
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Order not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentController.processPayment(paymentRequest);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void processPayment_OrderAlreadyHasPayment() {
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException("Order already has a payment"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentController.processPayment(paymentRequest);
        });

        assertEquals("Order already has a payment", exception.getMessage());
        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void getPaymentByOrderId_Success() {
        when(paymentService.getPaymentByOrderId(anyLong())).thenReturn(payment);

        ResponseEntity<PaymentResponse> response = paymentController.getPaymentByOrderId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TXN-12345678", response.getBody().getTransactionId());
        assertEquals(new BigDecimal("100.00"), response.getBody().getAmount());
        assertEquals("COMPLETED", response.getBody().getStatus());

        verify(paymentService).getPaymentByOrderId(1L);
    }

    @Test
    void getPaymentByOrderId_NotFound() {
        when(paymentService.getPaymentByOrderId(anyLong()))
                .thenThrow(new RuntimeException("Payment not found for order"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentController.getPaymentByOrderId(1L);
        });

        assertEquals("Payment not found for order", exception.getMessage());
        verify(paymentService).getPaymentByOrderId(1L);
    }

    @Test
    void getPaymentById_Success() {
        when(paymentService.getPaymentById(anyLong())).thenReturn(payment);

        ResponseEntity<PaymentResponse> response = paymentController.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TXN-12345678", response.getBody().getTransactionId());
        assertEquals(new BigDecimal("100.00"), response.getBody().getAmount());
        assertEquals("COMPLETED", response.getBody().getStatus());

        verify(paymentService).getPaymentById(1L);
    }

    @Test
    void getPaymentById_NotFound() {
        when(paymentService.getPaymentById(anyLong()))
                .thenThrow(new RuntimeException("Payment not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentController.getPaymentById(1L);
        });

        assertEquals("Payment not found", exception.getMessage());
        verify(paymentService).getPaymentById(1L);
    }

    @Test
    void processPayment_WithDebitCard() {
        paymentRequest.setPaymentMethod(Payment.PaymentMethod.DEBIT_CARD);
        payment.setPaymentMethod(Payment.PaymentMethod.DEBIT_CARD);

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(payment);

        ResponseEntity<PaymentResponse> response = paymentController.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DEBIT_CARD", response.getBody().getPaymentMethod());

        verify(paymentService).processPayment(any(PaymentRequest.class));
    }

    @Test
    void processPayment_WithPSE() {
        paymentRequest.setPaymentMethod(Payment.PaymentMethod.PSE);
        payment.setPaymentMethod(Payment.PaymentMethod.PSE);

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(payment);

        ResponseEntity<PaymentResponse> response = paymentController.processPayment(paymentRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PSE", response.getBody().getPaymentMethod());

        verify(paymentService).processPayment(any(PaymentRequest.class));
    }
}
