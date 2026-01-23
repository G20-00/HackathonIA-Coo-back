package com.coomeva.hackathon.dto;

import com.coomeva.hackathon.entity.Service.ServiceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateServiceRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Boolean active = true;
    private String imageUrl;
    private ServiceType type;
}
