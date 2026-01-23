package com.coomeva.hackathon.dto;

import com.coomeva.hackathon.entity.Order;
import com.coomeva.hackathon.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String userName;
    private String userEmail;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        response.setUserEmail(order.getUser().getEmail());
        response.setItems(order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList()));
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long serviceId;
        private String serviceName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;

        public static OrderItemResponse fromEntity(OrderItem item) {
            OrderItemResponse response = new OrderItemResponse();
            response.setId(item.getId());
            response.setServiceId(item.getService().getId());
            response.setServiceName(item.getService().getName());
            response.setQuantity(item.getQuantity());
            response.setPrice(item.getPrice());
            response.setSubtotal(item.getSubtotal());
            return response;
        }
    }
}
