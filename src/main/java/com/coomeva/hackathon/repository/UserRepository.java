package com.coomeva.hackathon.repository;

import com.coomeva.hackathon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
    boolean existsByDocumentNumber(String documentNumber);
}
