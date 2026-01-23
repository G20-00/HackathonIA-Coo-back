package com.coomeva.hackathon.service;

import com.coomeva.hackathon.entity.Service;
import com.coomeva.hackathon.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

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
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    @Transactional
    public Service updateService(Long id, Service serviceDetails) {
        Service service = getServiceById(id);
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setPrice(serviceDetails.getPrice());
        service.setImageUrl(serviceDetails.getImageUrl());
        service.setAvailable(serviceDetails.getAvailable());
        service.setType(serviceDetails.getType());
        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
