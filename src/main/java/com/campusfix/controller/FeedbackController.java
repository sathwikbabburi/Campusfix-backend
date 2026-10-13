package com.campusfix.controller;

import com.campusfix.dto.ApiResponse;
import com.campusfix.dto.FeedbackRequest;
import com.campusfix.dto.FeedbackResponse;
import com.campusfix.model.User;
import com.campusfix.service.AuthService;
import com.campusfix.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@Tag(name = "Feedback & Satisfaction Rating", description = "Endpoints for student resolution feedback and 1-5 star ratings")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AuthService authService;

    public FeedbackController(FeedbackService feedbackService, AuthService authService) {
        this.feedbackService = feedbackService;
        this.authService = authService;
    }

    @PostMapping
    @Operation(summary = "Submit feedback and star rating (1-5) for resolved complaint")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        FeedbackResponse response = feedbackService.submitFeedback(request, currentUser);
        return new ResponseEntity<>(ApiResponse.success("Thank you for your feedback! Ticket has been closed.", response), HttpStatus.CREATED);
    }

    @GetMapping("/complaint/{complaintId}")
    @Operation(summary = "Get feedback associated with a complaint")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedbackByComplaint(@PathVariable Long complaintId) {
        FeedbackResponse response = feedbackService.getFeedbackByComplaintId(complaintId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
