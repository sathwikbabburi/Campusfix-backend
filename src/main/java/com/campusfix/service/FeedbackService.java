package com.campusfix.service;

import com.campusfix.dto.FeedbackRequest;
import com.campusfix.dto.FeedbackResponse;
import com.campusfix.exception.BadRequestException;
import com.campusfix.exception.DuplicateResourceException;
import com.campusfix.exception.ResourceNotFoundException;
import com.campusfix.exception.UnauthorizedException;
import com.campusfix.model.Complaint;
import com.campusfix.model.Feedback;
import com.campusfix.model.Status;
import com.campusfix.model.User;
import com.campusfix.repository.ComplaintRepository;
import com.campusfix.repository.FeedbackRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ComplaintRepository complaintRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, ComplaintRepository complaintRepository) {
        this.feedbackRepository = feedbackRepository;
        this.complaintRepository = complaintRepository;
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackRequest request, User currentUser) {
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", request.getComplaintId()));

        if (!complaint.getComplainant().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the student who submitted the complaint can provide feedback");
        }

        if (complaint.getStatus() != Status.RESOLVED && complaint.getStatus() != Status.CLOSED) {
            throw new BadRequestException("Feedback can only be provided after the issue has been resolved");
        }

        if (feedbackRepository.existsByComplaintId(complaint.getId())) {
            throw new DuplicateResourceException("Feedback has already been submitted for this complaint");
        }

        Feedback feedback = new Feedback(complaint, currentUser, request.getRating(), request.getComments());
        Feedback saved = feedbackRepository.save(feedback);

        // Auto close ticket once feedback received
        complaint.setStatus(Status.CLOSED);
        complaintRepository.save(complaint);

        return new FeedbackResponse(saved);
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackByComplaintId(Long complaintId) {
        Feedback feedback = feedbackRepository.findByComplaintId(complaintId)
                .orElse(null);
        return feedback != null ? new FeedbackResponse(feedback) : null;
    }
}
