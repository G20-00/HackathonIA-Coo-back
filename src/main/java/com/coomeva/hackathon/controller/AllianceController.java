package com.coomeva.hackathon.controller;

import com.coomeva.hackathon.entity.Alliance;
import com.coomeva.hackathon.service.AllianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alliances")
@RequiredArgsConstructor
@Tag(name = "Alliances", description = "Alliance management endpoints")
@CrossOrigin(origins = "*")
public class AllianceController {

    private final AllianceService allianceService;

    @GetMapping
    @Operation(summary = "Get all alliances")
    public ResponseEntity<List<Alliance>> getAllAlliances() {
        return ResponseEntity.ok(allianceService.getAllAlliances());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active alliances")
    public ResponseEntity<List<Alliance>> getActiveAlliances() {
        return ResponseEntity.ok(allianceService.getActiveAlliances());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get alliance by ID")
    public ResponseEntity<Alliance> getAllianceById(@PathVariable Long id) {
        return ResponseEntity.ok(allianceService.getAllianceById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search alliances")
    public ResponseEntity<List<Alliance>> searchAlliances(@RequestParam String keyword) {
        return ResponseEntity.ok(allianceService.searchAlliances(keyword));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new alliance (Admin only)")
    public ResponseEntity<Alliance> createAlliance(@RequestBody Alliance alliance) {
        return ResponseEntity.ok(allianceService.createAlliance(alliance));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update alliance (Admin only)")
    public ResponseEntity<Alliance> updateAlliance(@PathVariable Long id, @RequestBody Alliance alliance) {
        return ResponseEntity.ok(allianceService.updateAlliance(id, alliance));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete alliance (Admin only)")
    public ResponseEntity<Void> deleteAlliance(@PathVariable Long id) {
        allianceService.deleteAlliance(id);
        return ResponseEntity.noContent().build();
    }
}
