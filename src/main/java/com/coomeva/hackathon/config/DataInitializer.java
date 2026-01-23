package com.coomeva.hackathon.config;

import com.coomeva.hackathon.entity.*;
import com.coomeva.hackathon.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final AllianceRepository allianceRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            log.info("Initializing database with sample data...");
            initializeCategories();
            initializeServices();
            initializeAlliances();
            initializeUsers();
            log.info("Database initialization completed!");
        } else {
            log.info("Database already initialized.");
        }
    }

    private void initializeCategories() {
        List<Category> categories = Arrays.asList(
                createCategory("Salud", "Planes médicos y servicios de salud integral", "🏥"),
                createCategory("Educación", "Créditos educativos y programas de formación", "🎓"),
                createCategory("Seguros", "Protección para ti y tu familia", "🛡️"),
                createCategory("Créditos y Finanzas", "Soluciones financieras a tu medida", "💰"),
                createCategory("Recreación y Bienestar", "Actividades y beneficios para asociados", "🎉")
        );
        categoryRepository.saveAll(categories);
        log.info("Categories initialized: {}", categories.size());
    }

    private Category createCategory(String name, String description, String icon) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        return category;
    }

    private void initializeServices() {
        List<Category> categories = categoryRepository.findAll();

        // Servicios de Salud
        Category salud = categories.stream()
                .filter(c -> c.getName().equals("Salud"))
                .findFirst()
                .orElseThrow();

        List<Service> serviciosSalud = Arrays.asList(
                createService("Plan Médico Familiar", "Cobertura médica completa para toda la familia",
                        new BigDecimal("350000"), Service.ServiceType.SALUD, salud),
                createService("Consulta Médica General", "Atención médica general con profesionales expertos",
                        new BigDecimal("50000"), Service.ServiceType.SALUD, salud),
                createService("Odontología Integral", "Servicios odontológicos completos",
                        new BigDecimal("120000"), Service.ServiceType.SALUD, salud),
                createService("Exámenes de Laboratorio", "Paquete completo de exámenes clínicos",
                        new BigDecimal("80000"), Service.ServiceType.SALUD, salud)
        );

        // Servicios de Educación
        Category educacion = categories.stream()
                .filter(c -> c.getName().equals("Educación"))
                .findFirst()
                .orElseThrow();

        List<Service> serviciosEducacion = Arrays.asList(
                createService("Crédito Educativo Universitario", "Financiación para estudios universitarios",
                        new BigDecimal("10000000"), Service.ServiceType.EDUCACION, educacion),
                createService("Curso de Inglés", "Programa completo de inglés con certificación",
                        new BigDecimal("800000"), Service.ServiceType.EDUCACION, educacion),
                createService("Becas de Estudio", "Apoyo económico para estudiantes destacados",
                        new BigDecimal("2000000"), Service.ServiceType.EDUCACION, educacion),
                createService("Programa de Capacitación", "Cursos técnicos y profesionales",
                        new BigDecimal("500000"), Service.ServiceType.EDUCACION, educacion)
        );

        // Servicios de Seguros
        Category seguros = categories.stream()
                .filter(c -> c.getName().equals("Seguros"))
                .findFirst()
                .orElseThrow();

        List<Service> serviciosSeguros = Arrays.asList(
                createService("Seguro de Vida", "Protección financiera para tu familia",
                        new BigDecimal("150000"), Service.ServiceType.SEGUROS, seguros),
                createService("Seguro de Vehículo", "Cobertura completa para tu vehículo",
                        new BigDecimal("800000"), Service.ServiceType.SEGUROS, seguros),
                createService("Seguro de Hogar", "Protege tu hogar y tus bienes",
                        new BigDecimal("300000"), Service.ServiceType.SEGUROS, seguros),
                createService("Seguro de Salud Complementario", "Cobertura adicional para servicios médicos",
                        new BigDecimal("200000"), Service.ServiceType.SEGUROS, seguros)
        );

        // Servicios de Créditos
        Category creditos = categories.stream()
                .filter(c -> c.getName().equals("Créditos y Finanzas"))
                .findFirst()
                .orElseThrow();

        List<Service> serviciosCreditos = Arrays.asList(
                createService("Crédito Personal", "Préstamo personal con tasas preferenciales",
                        new BigDecimal("5000000"), Service.ServiceType.CREDITOS, creditos),
                createService("Crédito de Vivienda", "Financiación para compra de vivienda",
                        new BigDecimal("80000000"), Service.ServiceType.CREDITOS, creditos),
                createService("Cuenta de Ahorros", "Ahorra con intereses competitivos",
                        new BigDecimal("100000"), Service.ServiceType.CREDITOS, creditos),
                createService("Inversión a Plazo Fijo", "Rentabiliza tu dinero de forma segura",
                        new BigDecimal("1000000"), Service.ServiceType.CREDITOS, creditos)
        );

        // Servicios de Recreación
        Category recreacion = categories.stream()
                .filter(c -> c.getName().equals("Recreación y Bienestar"))
                .findFirst()
                .orElseThrow();

        List<Service> serviciosRecreacion = Arrays.asList(
                createService("Membresía Club Deportivo", "Acceso a instalaciones deportivas y recreativas",
                        new BigDecimal("250000"), Service.ServiceType.RECREACION, recreacion),
                createService("Paquete Turístico Nacional", "Viajes organizados por Colombia",
                        new BigDecimal("1500000"), Service.ServiceType.RECREACION, recreacion),
                createService("Actividades Culturales", "Eventos culturales y artísticos",
                        new BigDecimal("80000"), Service.ServiceType.RECREACION, recreacion),
                createService("Gimnasio y Spa", "Acceso a gimnasio y servicios de spa",
                        new BigDecimal("180000"), Service.ServiceType.RECREACION, recreacion)
        );

        serviceRepository.saveAll(serviciosSalud);
        serviceRepository.saveAll(serviciosEducacion);
        serviceRepository.saveAll(serviciosSeguros);
        serviceRepository.saveAll(serviciosCreditos);
        serviceRepository.saveAll(serviciosRecreacion);

        log.info("Services initialized: {}", serviceRepository.count());
    }

    private Service createService(String name, String description, BigDecimal price,
                                  Service.ServiceType type, Category category) {
        Service service = new Service();
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setType(type);
        service.setCategory(category);
        service.setAvailable(true);
        service.setImageUrl("https://via.placeholder.com/300");
        return service;
    }

    private void initializeAlliances() {
        List<Alliance> alliances = Arrays.asList(
                createAlliance("Clínica Valle del Lili", "Centro médico de alta complejidad",
                        "https://valledellili.org", "contacto@valledellili.org", "602-3319090"),
                createAlliance("Universidad ICESI", "Educación superior de calidad",
                        "https://icesi.edu.co", "info@icesi.edu.co", "602-5552334"),
                createAlliance("Hotel Intercontinental Cali", "Hospedaje y eventos corporativos",
                        "https://intercontinental.com", "reservas@intercontinental.com", "602-8827000"),
                createAlliance("Gimnasios Bodytech", "Cadena de gimnasios y bienestar",
                        "https://bodytech.com.co", "info@bodytech.com", "601-7440440"),
                createAlliance("Seguros Bolívar", "Productos de seguros integrales",
                        "https://segurosbolivar.com", "atencion@segurosbolivar.com", "01-8000-113300")
        );

        allianceRepository.saveAll(alliances);
        log.info("Alliances initialized: {}", alliances.size());
    }

    private Alliance createAlliance(String name, String description, String website,
                                   String email, String phone) {
        Alliance alliance = new Alliance();
        alliance.setName(name);
        alliance.setDescription(description);
        alliance.setWebsite(website);
        alliance.setContactEmail(email);
        alliance.setContactPhone(phone);
        alliance.setActive(true);
        alliance.setLogoUrl("https://via.placeholder.com/150");
        return alliance;
    }

    private void initializeUsers() {
        // Admin user
        User admin = new User();
        admin.setEmail("admin@coomeva.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("Coomeva");
        admin.setDocumentNumber("1000000000");
        admin.setPhone("3001234567");
        admin.setRole(User.UserRole.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        // Regular user
        User user = new User();
        user.setEmail("usuario@coomeva.com");
        user.setPassword(passwordEncoder.encode("usuario123"));
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setDocumentNumber("1000000001");
        user.setPhone("3007654321");
        user.setRole(User.UserRole.USER);
        user.setEnabled(true);
        userRepository.save(user);

        log.info("Users initialized: Admin and User accounts created");
        log.info("Admin credentials: admin@coomeva.com / admin123");
        log.info("User credentials: usuario@coomeva.com / usuario123");
    }
}
