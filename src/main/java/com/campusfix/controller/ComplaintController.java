package com.campusfix.controller;

import com.campusfix.dto.*;
import com.campusfix.model.Priority;
import com.campusfix.model.Status;
import com.campusfix.model.User;
import com.campusfix.service.AuthService;
import com.campusfix.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@Tag(name = "Complaints Management", description = "Endpoints for complaints CRUD, pagination, filtering, status tracking, and upvotes")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final AuthService authService;

    public ComplaintController(ComplaintService complaintService, AuthService authService) {
        this.complaintService = complaintService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "Lodge a new campus complaint (Student/Faculty/Staff)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(@Valid @RequestBody ComplaintCreateRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        ComplaintResponse response = complaintService.createComplaint(request, currentUser);
        return new ResponseEntity<>(ApiResponse.success("Complaint submitted successfully. Ticket: " + response.getTicketNumber(), response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get paginated, sorted, and filtered list of complaints")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintResponse>>> getAllComplaints(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        User currentUser = null;
        try {
            currentUser = authService.getCurrentAuthenticatedUser();
        } catch (Exception ignored) {}

        PageResponse<ComplaintResponse> response = complaintService.getAllComplaints(search, categoryId, status, priority, pageable, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed complaint by ID with lifecycle timeline and feedback")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(@PathVariable Long id) {
        User currentUser = null;
        try {
            currentUser = authService.getCurrentAuthenticatedUser();
        } catch (Exception ignored) {}

        return ResponseEntity.ok(ApiResponse.success(complaintService.getComplaintById(id, currentUser)));
    }

    @GetMapping("/ticket/{ticketNumber}")
    @Operation(summary = "Public complaint tracking by ticket number (e.g. CF-2026-001)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintByTicket(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getComplaintByTicketNumber(ticketNumber)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get complaints submitted by currently logged-in user")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getMyComplaints() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(complaintService.getMyComplaints(currentUser)));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Get complaints assigned to current maintenance technician (Staff/Admin)")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAssignedComplaints() {
        User staff = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(complaintService.getAssignedComplaints(staff)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update complaint details (Owner or Admin)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintUpdateRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success("Complaint updated successfully", complaintService.updateComplaint(id, request, currentUser)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Update complaint workflow status and record action note (Staff/Admin)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintStatusUpdateRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success("Status updated to " + request.getStatus(), complaintService.updateStatus(id, request, currentUser)));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign technician staff to complaint (Admin only)")
    public ResponseEntity<ApiResponse<ComplaintResponse>> assignStaff(
            @PathVariable Long id,
            @RequestParam Long staffId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success("Staff assigned successfully", complaintService.assignStaff(id, staffId, currentUser)));
    }

    @PostMapping("/{id}/upvote")
    @Operation(summary = "Toggle upvote on complaint to prevent duplicates & prioritize issue")
    public ResponseEntity<ApiResponse<Boolean>> toggleUpvote(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        boolean upvoted = complaintService.toggleUpvote(id, currentUser);
        String msg = upvoted ? "Complaint upvoted! Priority escalated." : "Upvote removed.";
        return ResponseEntity.ok(ApiResponse.success(msg, upvoted));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Get complete audit timeline history of complaint")
    public ResponseEntity<ApiResponse<List<ComplaintTimelineResponse>>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(complaintService.getTimelines(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete complaint (Complainant before assignment, or Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        complaintService.deleteComplaint(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted successfully", null));
    }
}
