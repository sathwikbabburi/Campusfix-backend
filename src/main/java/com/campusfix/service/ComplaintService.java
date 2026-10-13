package com.campusfix.service;

import com.campusfix.dto.*;
import com.campusfix.exception.BadRequestException;
import com.campusfix.exception.ResourceNotFoundException;
import com.campusfix.exception.UnauthorizedException;
import com.campusfix.model.*;
import com.campusfix.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private static final Logger log = LoggerFactory.getLogger(ComplaintService.class);

    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintTimelineRepository timelineRepository;
    private final ComplaintUpvoteRepository upvoteRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    public ComplaintService(ComplaintRepository complaintRepository,
                            CategoryRepository categoryRepository,
                            ComplaintTimelineRepository timelineRepository,
                            ComplaintUpvoteRepository upvoteRepository,
                            UserRepository userRepository,
                            FeedbackRepository feedbackRepository) {
        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.timelineRepository = timelineRepository;
        this.upvoteRepository = upvoteRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request, User complainant) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        // Generate Human-friendly Ticket Number (CF-YYYY-XXXX)
        long count = complaintRepository.count() + 1;
        String ticketNumber = String.format("CF-%d-%04d", Year.now().getValue(), count);

        Complaint complaint = new Complaint();
        complaint.setTicketNumber(ticketNumber);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(category);
        complaint.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        complaint.setStatus(Status.SUBMITTED);
        complaint.setLocationBlock(request.getLocationBlock());
        complaint.setRoomNumber(request.getRoomNumber());
        complaint.setComplainant(complainant);
        complaint.setImageUrl(request.getImageUrl());
        complaint.setUpvotesCount(0);

        // Smart Department Staff Auto-Assignment
        List<User> matchingStaff = userRepository.findByRole(Role.STAFF).stream()
                .filter(s -> s.getDepartment() != null &&
                        (s.getDepartment().toLowerCase().contains(category.getName().toLowerCase()) ||
                         category.getName().toLowerCase().contains(s.getDepartment().toLowerCase().replace("dept", "").trim())))
                .collect(Collectors.toList());

        if (!matchingStaff.isEmpty()) {
            User assigned = matchingStaff.get(0);
            complaint.setAssignedStaff(assigned);
            complaint.setStatus(Status.ASSIGNED);
        }

        Complaint savedComplaint = complaintRepository.save(complaint);

        // Timeline entry for creation
        ComplaintTimeline timeline = new ComplaintTimeline(
                savedComplaint,
                complainant,
                null,
                Status.SUBMITTED.name(),
                "Complaint lodged with initial details and location."
        );
        timelineRepository.save(timeline);

        if (savedComplaint.getAssignedStaff() != null) {
            ComplaintTimeline assignTimeline = new ComplaintTimeline(
                    savedComplaint,
                    savedComplaint.getAssignedStaff(),
                    Status.SUBMITTED.name(),
                    Status.ASSIGNED.name(),
                    "System auto-assigned to " + savedComplaint.getAssignedStaff().getFullName() + " based on category " + category.getName()
            );
            timelineRepository.save(assignTimeline);
        }

        log.info("Created complaint ticket: {} for user: {}", savedComplaint.getTicketNumber(), complainant.getEmail());
        return mapToResponse(savedComplaint, complainant);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long id, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));
        return mapToResponse(complaint, currentUser);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintByTicketNumber(String ticketNumber) {
        Complaint complaint = complaintRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "ticketNumber", ticketNumber));
        return mapToResponse(complaint, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<ComplaintResponse> getAllComplaints(String search, Long categoryId, Status status, Priority priority, Pageable pageable, User currentUser) {
        List<Complaint> all = complaintRepository.findAllByOrderByCreatedAtDesc();

        List<Complaint> filtered = all.stream().filter(c -> {
            if (search != null && !search.isBlank()) {
                String q = search.toLowerCase();
                boolean matchesTitle = c.getTitle() != null && c.getTitle().toLowerCase().contains(q);
                boolean matchesDesc = c.getDescription() != null && c.getDescription().toLowerCase().contains(q);
                boolean matchesTicket = c.getTicketNumber() != null && c.getTicketNumber().toLowerCase().contains(q);
                boolean matchesLoc = c.getLocationBlock() != null && c.getLocationBlock().toLowerCase().contains(q);
                if (!matchesTitle && !matchesDesc && !matchesTicket && !matchesLoc) return false;
            }
            if (categoryId != null && !c.getCategory().getId().equals(categoryId)) {
                return false;
            }
            if (status != null && c.getStatus() != status) {
                return false;
            }
            if (priority != null && c.getPriority() != priority) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Complaint> subList = (start > filtered.size()) ? List.of() : filtered.subList(start, end);

        List<ComplaintResponse> responseList = subList.stream()
                .map(c -> mapToResponse(c, currentUser))
                .collect(Collectors.toList());

        Page<ComplaintResponse> page = new PageImpl<>(responseList, pageable, filtered.size());
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getMyComplaints(User user) {
        return complaintRepository.findByComplainantIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(c -> mapToResponse(c, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAssignedComplaints(User staff) {
        return complaintRepository.findByAssignedStaffIdOrderByCreatedAtDesc(staff.getId()).stream()
                .map(c -> mapToResponse(c, staff))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    @Transactional
    public ComplaintResponse updateComplaint(Long id, ComplaintUpdateRequest request, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        boolean isOwner = complaint.getComplainant().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to edit this complaint");
        }

        if (complaint.getStatus() == Status.RESOLVED || complaint.getStatus() == Status.CLOSED) {
            throw new BadRequestException("Resolved or closed complaints cannot be edited");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(category);
        if (request.getPriority() != null) {
            complaint.setPriority(request.getPriority());
        }
        complaint.setLocationBlock(request.getLocationBlock());
        complaint.setRoomNumber(request.getRoomNumber());
        complaint.setImageUrl(request.getImageUrl());
        complaint.setUpdatedAt(LocalDateTime.now());

        Complaint saved = complaintRepository.save(complaint);

        ComplaintTimeline timeline = new ComplaintTimeline(
                saved,
                currentUser,
                saved.getStatus().name(),
                saved.getStatus().name(),
                "Complaint details updated by " + currentUser.getFullName()
        );
        timelineRepository.save(timeline);

        return mapToResponse(saved, currentUser);
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    @Transactional
    public ComplaintResponse updateStatus(Long id, ComplaintStatusUpdateRequest request, User updatedBy) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        String oldStatus = complaint.getStatus().name();
        complaint.setStatus(request.getStatus());
        complaint.setUpdatedAt(LocalDateTime.now());

        if (request.getStatus() == Status.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
            complaint.setResolutionNotes(request.getResolutionNotes());
            complaint.setResolutionImageUrl(request.getResolutionImageUrl());
        }

        if (request.getAssignedStaffId() != null) {
            User staff = userRepository.findById(request.getAssignedStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedStaffId()));
            complaint.setAssignedStaff(staff);
        }

        Complaint saved = complaintRepository.save(complaint);

        ComplaintTimeline timeline = new ComplaintTimeline(
                saved,
                updatedBy,
                oldStatus,
                request.getStatus().name(),
                request.getActionComment() != null ? request.getActionComment() : "Status updated to " + request.getStatus()
        );
        timelineRepository.save(timeline);

        log.info("Ticket {} status updated from {} to {} by {}", saved.getTicketNumber(), oldStatus, request.getStatus(), updatedBy.getEmail());
        return mapToResponse(saved, updatedBy);
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    @Transactional
    public ComplaintResponse assignStaff(Long id, Long staffId, User assignedBy) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        complaint.setAssignedStaff(staff);
        if (complaint.getStatus() == Status.SUBMITTED) {
            complaint.setStatus(Status.ASSIGNED);
        }
        complaint.setUpdatedAt(LocalDateTime.now());

        Complaint saved = complaintRepository.save(complaint);

        ComplaintTimeline timeline = new ComplaintTimeline(
                saved,
                assignedBy,
                complaint.getStatus().name(),
                Status.ASSIGNED.name(),
                "Assigned to staff technician " + staff.getFullName() + " (" + staff.getDepartment() + ")"
        );
        timelineRepository.save(timeline);

        return mapToResponse(saved, assignedBy);
    }

    @Transactional
    public boolean toggleUpvote(Long complaintId, User user) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", complaintId));

        Optional<ComplaintUpvote> existing = upvoteRepository.findByComplaintIdAndUserId(complaintId, user.getId());

        boolean nowUpvoted;
        if (existing.isPresent()) {
            upvoteRepository.delete(existing.get());
            complaint.setUpvotesCount(Math.max(0, (complaint.getUpvotesCount() != null ? complaint.getUpvotesCount() : 1) - 1));
            nowUpvoted = false;
        } else {
            ComplaintUpvote upvote = new ComplaintUpvote(complaint, user);
            upvoteRepository.save(upvote);
            complaint.setUpvotesCount((complaint.getUpvotesCount() != null ? complaint.getUpvotesCount() : 0) + 1);
            nowUpvoted = true;

            // Priority escalation: If 5+ students upvote, escalate priority
            if (complaint.getUpvotesCount() >= 5 && complaint.getPriority() == Priority.LOW) {
                complaint.setPriority(Priority.MEDIUM);
            } else if (complaint.getUpvotesCount() >= 10 && complaint.getPriority() == Priority.MEDIUM) {
                complaint.setPriority(Priority.HIGH);
            }
        }

        complaintRepository.save(complaint);
        return nowUpvoted;
    }

    @CacheEvict(value = "dashboardStats", allEntries = true)
    @Transactional
    public void deleteComplaint(Long id, User user) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));

        boolean isOwner = complaint.getComplainant().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("Not authorized to delete this complaint");
        }

        if (complaint.getStatus() != Status.SUBMITTED && !isAdmin) {
            throw new BadRequestException("Complaints that have already been assigned or started cannot be deleted");
        }

        complaintRepository.delete(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintTimelineResponse> getTimelines(Long complaintId) {
        return timelineRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId).stream()
                .map(ComplaintTimelineResponse::new)
                .collect(Collectors.toList());
    }

    private ComplaintResponse mapToResponse(Complaint complaint, User currentUser) {
        ComplaintResponse response = new ComplaintResponse(complaint);

        // Populate Timeline
        List<ComplaintTimelineResponse> timelines = timelineRepository.findByComplaintIdOrderByCreatedAtAsc(complaint.getId())
                .stream()
                .map(ComplaintTimelineResponse::new)
                .collect(Collectors.toList());
        response.setTimeline(timelines);

        // Populate Feedback
        Optional<Feedback> feedback = feedbackRepository.findByComplaintId(complaint.getId());
        feedback.ifPresent(f -> response.setFeedback(new FeedbackResponse(f)));

        // Check if current user has upvoted
        if (currentUser != null) {
            boolean hasUpvoted = upvoteRepository.existsByComplaintIdAndUserId(complaint.getId(), currentUser.getId());
            response.setHasUpvoted(hasUpvoted);
        }

        return response;
    }
}
