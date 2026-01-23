package com.coomeva.hackathon.repository;

import com.coomeva.hackathon.entity.Alliance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllianceRepository extends JpaRepository<Alliance, Long> {
    List<Alliance> findByActive(Boolean active);
    List<Alliance> findByNameContainingIgnoreCase(String name);
}
