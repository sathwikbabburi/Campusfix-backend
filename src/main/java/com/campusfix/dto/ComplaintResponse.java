package com.campusfix.dto;

import com.campusfix.model.Category;
import com.campusfix.model.Complaint;
import com.campusfix.model.Priority;
import com.campusfix.model.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintResponse {

    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private String locationBlock;
    private String roomNumber;
    private UserSummaryDto complainant;
    private UserSummaryDto assignedStaff;
    private String imageUrl;
    private String resolutionImageUrl;
    private String resolutionNotes;
    private Integer upvotesCount;
    private boolean hasUpvoted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private List<ComplaintTimelineResponse> timeline = new ArrayList<>();
    private FeedbackResponse feedback;

    public ComplaintResponse() {}

    public ComplaintResponse(Complaint complaint) {
        if (complaint != null) {
            this.id = complaint.getId();
            this.ticketNumber = complaint.getTicketNumber();
            this.title = complaint.getTitle();
            this.description = complaint.getDescription();
            this.category = complaint.getCategory();
            this.priority = complaint.getPriority();
            this.status = complaint.getStatus();
            this.locationBlock = complaint.getLocationBlock();
            this.roomNumber = complaint.getRoomNumber();
            this.imageUrl = complaint.getImageUrl();
            this.resolutionImageUrl = complaint.getResolutionImageUrl();
            this.resolutionNotes = complaint.getResolutionNotes();
            this.upvotesCount = complaint.getUpvotesCount() != null ? complaint.getUpvotesCount() : 0;
            this.createdAt = complaint.getCreatedAt();
            this.updatedAt = complaint.getUpdatedAt();
            this.resolvedAt = complaint.getResolvedAt();
            if (complaint.getComplainant() != null) {
                this.complainant = new UserSummaryDto(complaint.getComplainant());
            }
            if (complaint.getAssignedStaff() != null) {
                this.assignedStaff = new UserSummaryDto(complaint.getAssignedStaff());
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getLocationBlock() { return locationBlock; }
    public void setLocationBlock(String locationBlock) { this.locationBlock = locationBlock; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public UserSummaryDto getComplainant() { return complainant; }
    public void setComplainant(UserSummaryDto complainant) { this.complainant = complainant; }

    public UserSummaryDto getAssignedStaff() { return assignedStaff; }
    public void setAssignedStaff(UserSummaryDto assignedStaff) { this.assignedStaff = assignedStaff; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getResolutionImageUrl() { return resolutionImageUrl; }
    public void setResolutionImageUrl(String resolutionImageUrl) { this.resolutionImageUrl = resolutionImageUrl; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public Integer getUpvotesCount() { return upvotesCount; }
    public void setUpvotesCount(Integer upvotesCount) { this.upvotesCount = upvotesCount; }

    public boolean isHasUpvoted() { return hasUpvoted; }
    public void setHasUpvoted(boolean hasUpvoted) { this.hasUpvoted = hasUpvoted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public List<ComplaintTimelineResponse> getTimeline() { return timeline; }
    public void setTimeline(List<ComplaintTimelineResponse> timeline) { this.timeline = timeline; }

    public FeedbackResponse getFeedback() { return feedback; }
    public void setFeedback(FeedbackResponse feedback) { this.feedback = feedback; }
}
