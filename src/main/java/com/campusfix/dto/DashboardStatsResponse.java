package com.campusfix.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DashboardStatsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private long totalComplaints;
    private long submittedComplaints;
    private long assignedComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long criticalComplaints;
    private long totalUsers;
    private double averageRating;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByStatus;
    private Map<String, Long> complaintsByLocation;
    private List<ComplaintResponse> recentComplaints;

    public DashboardStatsResponse() {}

    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

    public long getSubmittedComplaints() { return submittedComplaints; }
    public void setSubmittedComplaints(long submittedComplaints) { this.submittedComplaints = submittedComplaints; }

    public long getAssignedComplaints() { return assignedComplaints; }
    public void setAssignedComplaints(long assignedComplaints) { this.assignedComplaints = assignedComplaints; }

    public long getInProgressComplaints() { return inProgressComplaints; }
    public void setInProgressComplaints(long inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; }

    public long getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    public long getCriticalComplaints() { return criticalComplaints; }
    public void setCriticalComplaints(long criticalComplaints) { this.criticalComplaints = criticalComplaints; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public Map<String, Long> getComplaintsByCategory() { return complaintsByCategory; }
    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) { this.complaintsByCategory = complaintsByCategory; }

    public Map<String, Long> getComplaintsByStatus() { return complaintsByStatus; }
    public void setComplaintsByStatus(Map<String, Long> complaintsByStatus) { this.complaintsByStatus = complaintsByStatus; }

    public Map<String, Long> getComplaintsByLocation() { return complaintsByLocation; }
    public void setComplaintsByLocation(Map<String, Long> complaintsByLocation) { this.complaintsByLocation = complaintsByLocation; }

    public List<ComplaintResponse> getRecentComplaints() { return recentComplaints; }
    public void setRecentComplaints(List<ComplaintResponse> recentComplaints) { this.recentComplaints = recentComplaints; }
}
