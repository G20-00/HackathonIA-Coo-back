package com.coomeva.hackathon.service;

import com.coomeva.hackathon.dto.CreateServiceRequest;
import com.coomeva.hackathon.entity.Category;
import com.coomeva.hackathon.entity.Service;
import com.coomeva.hackathon.repository.CategoryRepository;
import com.coomeva.hackathon.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ServiceService serviceService;

    private Category category;
    private Service service;
    private CreateServiceRequest createServiceRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Salud");
        category.setDescription("Servicios de salud");

        service = new Service();
        service.setId(1L);
        service.setName("Consulta Médica");
        service.setDescription("Consulta general");
        service.setPrice(new BigDecimal("50.00"));
        service.setAvailable(true);
        service.setType(Service.ServiceType.SALUD);
        service.setCategory(category);
        service.setImageUrl("http://example.com/image.jpg");

        createServiceRequest = new CreateServiceRequest();
        createServiceRequest.setName("Consulta Médica");
        createServiceRequest.setDescription("Consulta general");
        createServiceRequest.setPrice(new BigDecimal("50.00"));
        createServiceRequest.setCategoryId(1L);
        createServiceRequest.setActive(true);
        createServiceRequest.setType(Service.ServiceType.SALUD);
        createServiceRequest.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void getAllServices_Success() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findAll()).thenReturn(services);

        List<Service> result = serviceService.getAllServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository).findAll();
    }

    @Test
    void getAvailableServices_Success() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByAvailable(true)).thenReturn(services);

        List<Service> result = serviceService.getAvailableServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
        verify(serviceRepository).findByAvailable(true);
    }

    @Test
    void getServiceById_Success() {
        when(serviceRepository.findById(anyLong())).thenReturn(Optional.of(service));

        Service result = serviceService.getServiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Consulta Médica", result.getName());
        verify(serviceRepository).findById(1L);
    }

    @Test
    void getServiceById_NotFound() {
        when(serviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.getServiceById(1L);
        });

        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(serviceRepository).findById(1L);
    }

    @Test
    void getServicesByCategory_Success() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByCategoryId(anyLong())).thenReturn(services);

        List<Service> result = serviceService.getServicesByCategory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository).findByCategoryId(1L);
    }

    @Test
    void getServicesByType_Success() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByType(any(Service.ServiceType.class))).thenReturn(services);

        List<Service> result = serviceService.getServicesByType(Service.ServiceType.SALUD);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Service.ServiceType.SALUD, result.get(0).getType());
        verify(serviceRepository).findByType(Service.ServiceType.SALUD);
    }

    @Test
    void searchServices_Success() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(services);

        List<Service> result = serviceService.searchServices("Consulta");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository).findByNameContainingIgnoreCase("Consulta");
    }

    @Test
    void createService_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
            Service savedService = invocation.getArgument(0);
            savedService.setId(1L);
            return savedService;
        });

        Service result = serviceService.createService(createServiceRequest);

        assertNotNull(result);
        assertEquals("Consulta Médica", result.getName());
        assertEquals("Consulta general", result.getDescription());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
        assertEquals(category, result.getCategory());
        assertTrue(result.getAvailable());
        assertEquals(Service.ServiceType.SALUD, result.getType());

        verify(categoryRepository).findById(1L);
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void createService_CategoryNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.createService(createServiceRequest);
        });

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void createService_WithDefaults() {
        createServiceRequest.setActive(null);
        createServiceRequest.setType(null);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
            Service savedService = invocation.getArgument(0);
            savedService.setId(1L);
            return savedService;
        });

        Service result = serviceService.createService(createServiceRequest);

        assertNotNull(result);
        assertTrue(result.getAvailable()); // Default is true
        assertEquals(Service.ServiceType.SALUD, result.getType()); // Default is SALUD

        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void updateService_Success() {
        CreateServiceRequest updateRequest = new CreateServiceRequest();
        updateRequest.setName("Consulta Especializada");
        updateRequest.setPrice(new BigDecimal("75.00"));
        updateRequest.setCategoryId(1L);

        when(serviceRepository.findById(anyLong())).thenReturn(Optional.of(service));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        Service result = serviceService.updateService(1L, updateRequest);

        assertNotNull(result);
        verify(serviceRepository).findById(1L);
        verify(categoryRepository).findById(1L);
        verify(serviceRepository).save(service);
    }

    @Test
    void updateService_PartialUpdate() {
        CreateServiceRequest updateRequest = new CreateServiceRequest();
        updateRequest.setName("Consulta Especializada");

        when(serviceRepository.findById(anyLong())).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        Service result = serviceService.updateService(1L, updateRequest);

        assertNotNull(result);
        verify(serviceRepository).findById(1L);
        verify(categoryRepository, never()).findById(anyLong());
        verify(serviceRepository).save(service);
    }

    @Test
    void updateService_ServiceNotFound() {
        when(serviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.updateService(1L, createServiceRequest);
        });

        assertEquals("Service not found with id: 1", exception.getMessage());
        verify(serviceRepository).findById(1L);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void updateService_CategoryNotFound() {
        when(serviceRepository.findById(anyLong())).thenReturn(Optional.of(service));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceService.updateService(1L, createServiceRequest);
        });

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(serviceRepository).findById(1L);
        verify(categoryRepository).findById(1L);
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void deleteService_Success() {
        doNothing().when(serviceRepository).deleteById(anyLong());

        serviceService.deleteService(1L);

        verify(serviceRepository).deleteById(1L);
    }
}
