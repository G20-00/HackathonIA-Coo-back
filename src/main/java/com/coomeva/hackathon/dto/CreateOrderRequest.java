package com.coomeva.hackathon.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
    private String notes;
}
