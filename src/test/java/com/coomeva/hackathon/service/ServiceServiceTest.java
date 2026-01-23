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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private CreateServiceRequest request;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Salud");

        service = new Service();
        service.setId(1L);
        service.setName("Plan Médico");
        service.setDescription("Cobertura médica");
        service.setPrice(new BigDecimal("100000"));
        service.setCategory(category);
        service.setAvailable(true);
        service.setType(Service.ServiceType.SALUD);

        request = new CreateServiceRequest();
        request.setName("Plan Médico");
        request.setDescription("Cobertura médica");
        request.setPrice(new BigDecimal("100000"));
        request.setCategoryId(1L);
        request.setActive(true);
        request.setType(Service.ServiceType.SALUD);
    }

    @Test
    void getAllServices_ShouldReturnAllServices() {
        // Given
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findAll()).thenReturn(services);

        // When
        List<Service> result = serviceService.getAllServices();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Plan Médico");
        verify(serviceRepository).findAll();
    }

    @Test
    void getAvailableServices_ShouldReturnOnlyAvailableServices() {
        // Given
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByAvailable(true)).thenReturn(services);

        // When
        List<Service> result = serviceService.getAvailableServices();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAvailable()).isTrue();
        verify(serviceRepository).findByAvailable(true);
    }

    @Test
    void getServiceById_WhenExists_ShouldReturnService() {
        // Given
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        // When
        Service result = serviceService.getServiceById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Plan Médico");
        verify(serviceRepository).findById(1L);
    }

    @Test
    void getServiceById_WhenNotExists_ShouldThrowException() {
        // Given
        when(serviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> serviceService.getServiceById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Service not found");
    }

    @Test
    void createService_WithValidData_ShouldCreateService() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        // When
        Service result = serviceService.createService(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Plan Médico");
        verify(categoryRepository).findById(1L);
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void createService_WithInvalidCategory_ShouldThrowException() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> serviceService.createService(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void updateService_WithValidData_ShouldUpdateService() {
        // Given
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        request.setName("Plan Médico Actualizado");
        request.setPrice(new BigDecimal("150000"));

        // When
        Service result = serviceService.updateService(1L, request);

        // Then
        assertThat(result).isNotNull();
        verify(serviceRepository).findById(1L);
        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void deleteService_ShouldCallRepository() {
        // Given
        doNothing().when(serviceRepository).deleteById(1L);

        // When
        serviceService.deleteService(1L);

        // Then
        verify(serviceRepository).deleteById(1L);
    }

    @Test
    void getServicesByCategory_ShouldReturnServicesInCategory() {
        // Given
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByCategoryId(1L)).thenReturn(services);

        // When
        List<Service> result = serviceService.getServicesByCategory(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(serviceRepository).findByCategoryId(1L);
    }

    @Test
    void searchServices_ShouldReturnMatchingServices() {
        // Given
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByNameContainingIgnoreCase("Médico")).thenReturn(services);

        // When
        List<Service> result = serviceService.searchServices("Médico");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Médico");
        verify(serviceRepository).findByNameContainingIgnoreCase("Médico");
    }
}
