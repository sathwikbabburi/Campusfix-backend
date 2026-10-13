package com.campusfix.controller;

import com.campusfix.dto.AnnouncementRequest;
import com.campusfix.dto.ApiResponse;
import com.campusfix.model.Announcement;
import com.campusfix.model.User;
import com.campusfix.service.AnnouncementService;
import com.campusfix.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Campus Maintenance Announcements", description = "Broadcast notices for scheduled maintenance and campus advisories")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AuthService authService;

    public AnnouncementController(AnnouncementService announcementService, AuthService authService) {
        this.announcementService = announcementService;
        this.authService = authService;
    }

    @GetMapping("/active")
    @Operation(summary = "Get active campus maintenance announcements (Public)")
    public ResponseEntity<ApiResponse<List<Announcement>>> getActiveAnnouncements() {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getActiveAnnouncements()));
    }

    @GetMapping
    @Operation(summary = "Get all announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> getAllAnnouncements() {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getAllAnnouncements()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Post new campus announcement (Admin only)")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        User admin = authService.getCurrentAuthenticatedUser();
        Announcement saved = announcementService.createAnnouncement(request, admin);
        return new ResponseEntity<>(ApiResponse.success("Announcement published successfully", saved), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete announcement (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully", null));
    }
}
