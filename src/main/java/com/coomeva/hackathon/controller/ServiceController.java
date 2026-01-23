package com.coomeva.hackathon.controller;

import com.coomeva.hackathon.dto.CreateServiceRequest;
import com.coomeva.hackathon.entity.Service;
import com.coomeva.hackathon.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service management endpoints")
@CrossOrigin(origins = "*")
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    @Operation(summary = "Get all services")
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/available")
    @Operation(summary = "Get available services")
    public ResponseEntity<List<Service>> getAvailableServices() {
        return ResponseEntity.ok(serviceService.getAvailableServices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get services by category")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(serviceService.getServicesByCategory(categoryId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get services by type")
    public ResponseEntity<List<Service>> getServicesByType(@PathVariable Service.ServiceType type) {
        return ResponseEntity.ok(serviceService.getServicesByType(type));
    }

    @GetMapping("/search")
    @Operation(summary = "Search services")
    public ResponseEntity<List<Service>> searchServices(@RequestParam String keyword) {
        return ResponseEntity.ok(serviceService.searchServices(keyword));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new service (Admin only)")
    public ResponseEntity<Service> createService(@RequestBody CreateServiceRequest request) {
        return ResponseEntity.ok(serviceService.createService(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update service (Admin only)")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody CreateServiceRequest request) {
        return ResponseEntity.ok(serviceService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete service (Admin only)")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
