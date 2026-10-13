package com.campusfix.dto;

import com.campusfix.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private Status status;

    @NotBlank(message = "Action comment or progress note is required")
    private String actionComment;

    private Long assignedStaffId;
    private String resolutionNotes;
    private String resolutionImageUrl;

    public ComplaintStatusUpdateRequest() {}

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getActionComment() { return actionComment; }
    public void setActionComment(String actionComment) { this.actionComment = actionComment; }

    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getResolutionImageUrl() { return resolutionImageUrl; }
    public void setResolutionImageUrl(String resolutionImageUrl) { this.resolutionImageUrl = resolutionImageUrl; }
}
