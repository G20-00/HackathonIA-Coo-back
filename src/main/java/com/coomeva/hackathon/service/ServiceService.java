package com.coomeva.hackathon.service;

import com.coomeva.hackathon.dto.CreateServiceRequest;
import com.coomeva.hackathon.entity.Category;
import com.coomeva.hackathon.entity.Service;
import com.coomeva.hackathon.repository.CategoryRepository;
import com.coomeva.hackathon.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<Service> getAvailableServices() {
        return serviceRepository.findByAvailable(true);
    }

    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    public List<Service> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryId(categoryId);
    }

    public List<Service> getServicesByType(Service.ServiceType type) {
        return serviceRepository.findByType(type);
    }

    public List<Service> searchServices(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public Service createService(CreateServiceRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
        
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setCategory(category);
        service.setAvailable(request.getActive() != null ? request.getActive() : true);
        service.setImageUrl(request.getImageUrl());
        service.setType(request.getType() != null ? request.getType() : Service.ServiceType.SALUD);
        
        return serviceRepository.save(service);
    }

    @Transactional
    public Service updateService(Long id, CreateServiceRequest request) {
        Service service = getServiceById(id);
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
            service.setCategory(category);
        }
        
        if (request.getName() != null) service.setName(request.getName());
        if (request.getDescription() != null) service.setDescription(request.getDescription());
        if (request.getPrice() != null) service.setPrice(request.getPrice());
        if (request.getImageUrl() != null) service.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) service.setAvailable(request.getActive());
        if (request.getType() != null) service.setType(request.getType());
        
        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
