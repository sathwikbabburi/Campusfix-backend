package com.campusfix.dto;

import com.campusfix.model.ComplaintTimeline;
import java.time.LocalDateTime;

public class ComplaintTimelineResponse {
    private Long id;
    private String previousStatus;
    private String newStatus;
    private String actionComment;
    private LocalDateTime createdAt;
    private UserSummaryDto updatedBy;

    public ComplaintTimelineResponse() {}

    public ComplaintTimelineResponse(ComplaintTimeline timeline) {
        if (timeline != null) {
            this.id = timeline.getId();
            this.previousStatus = timeline.getPreviousStatus();
            this.newStatus = timeline.getNewStatus();
            this.actionComment = timeline.getActionComment();
            this.createdAt = timeline.getCreatedAt();
            if (timeline.getUpdatedBy() != null) {
                this.updatedBy = new UserSummaryDto(timeline.getUpdatedBy());
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getActionComment() { return actionComment; }
    public void setActionComment(String actionComment) { this.actionComment = actionComment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public UserSummaryDto getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UserSummaryDto updatedBy) { this.updatedBy = updatedBy; }
}
