package com.coomeva.hackathon.service;

import com.coomeva.hackathon.entity.Alliance;
import com.coomeva.hackathon.repository.AllianceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllianceService {

    private final AllianceRepository allianceRepository;

    public List<Alliance> getAllAlliances() {
        return allianceRepository.findAll();
    }

    public List<Alliance> getActiveAlliances() {
        return allianceRepository.findByActive(true);
    }

    public Alliance getAllianceById(Long id) {
        return allianceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alliance not found with id: " + id));
    }

    public List<Alliance> searchAlliances(String keyword) {
        return allianceRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public Alliance createAlliance(Alliance alliance) {
        return allianceRepository.save(alliance);
    }

    @Transactional
    public Alliance updateAlliance(Long id, Alliance allianceDetails) {
        Alliance alliance = getAllianceById(id);
        alliance.setName(allianceDetails.getName());
        alliance.setDescription(allianceDetails.getDescription());
        alliance.setLogoUrl(allianceDetails.getLogoUrl());
        alliance.setContactEmail(allianceDetails.getContactEmail());
        alliance.setContactPhone(allianceDetails.getContactPhone());
        alliance.setWebsite(allianceDetails.getWebsite());
        alliance.setActive(allianceDetails.getActive());
        return allianceRepository.save(alliance);
    }

    @Transactional
    public void deleteAlliance(Long id) {
        allianceRepository.deleteById(id);
    }
}
