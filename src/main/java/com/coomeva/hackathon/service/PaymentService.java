package com.coomeva.hackathon.service;

import com.coomeva.hackathon.dto.PaymentRequest;
import com.coomeva.hackathon.entity.Order;
import com.coomeva.hackathon.entity.Payment;
import com.coomeva.hackathon.repository.OrderRepository;
import com.coomeva.hackathon.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPayment() != null) {
            throw new RuntimeException("Order already has a payment");
        }

        // Simulate payment processing (sandbox mode)
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Simulate payment gateway response (always successful in sandbox)
        boolean paymentSuccessful = simulatePaymentGateway(request);

        if (paymentSuccessful) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            payment.setGatewayResponse("Payment processed successfully - SANDBOX MODE");
            
            // Update order status
            order.setStatus(Order.OrderStatus.COMPLETED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setGatewayResponse("Payment failed - SANDBOX MODE");
        }

        return paymentRepository.save(payment);
    }

    private boolean simulatePaymentGateway(PaymentRequest request) {
        // Simulate payment processing delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // In sandbox mode, all payments are successful
        return true;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
