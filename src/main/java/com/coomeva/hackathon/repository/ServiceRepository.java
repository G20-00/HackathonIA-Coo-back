package com.coomeva.hackathon.repository;

import com.coomeva.hackathon.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCategoryId(Long categoryId);
    List<Service> findByType(Service.ServiceType type);
    List<Service> findByAvailable(Boolean available);
    List<Service> findByNameContainingIgnoreCase(String name);
}
