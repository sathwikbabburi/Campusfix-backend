package com.campusfix.service;

import com.campusfix.dto.ComplaintResponse;
import com.campusfix.dto.DashboardStatsResponse;
import com.campusfix.model.Priority;
import com.campusfix.model.Status;
import com.campusfix.repository.ComplaintRepository;
import com.campusfix.repository.FeedbackRepository;
import com.campusfix.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    public DashboardService(ComplaintRepository complaintRepository,
                            UserRepository userRepository,
                            FeedbackRepository feedbackRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Cacheable(value = "dashboardStats")
    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        log.info("Calculating comprehensive dashboard analytics (cache miss)");

        DashboardStatsResponse stats = new DashboardStatsResponse();

        stats.setTotalComplaints(complaintRepository.count());
        stats.setSubmittedComplaints(complaintRepository.countByStatus(Status.SUBMITTED));
        stats.setAssignedComplaints(complaintRepository.countByStatus(Status.ASSIGNED));
        stats.setInProgressComplaints(complaintRepository.countByStatus(Status.IN_PROGRESS));
        stats.setResolvedComplaints(complaintRepository.countByStatus(Status.RESOLVED) + complaintRepository.countByStatus(Status.CLOSED));
        stats.setCriticalComplaints(complaintRepository.countByPriority(Priority.CRITICAL));
        stats.setTotalUsers(userRepository.count());

        Double avgRating = feedbackRepository.getAverageRating();
        stats.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 4.5);

        // Category breakdown
        Map<String, Long> categoryMap = new HashMap<>();
        List<Object[]> catResults = complaintRepository.countComplaintsByCategory();
        for (Object[] row : catResults) {
            categoryMap.put((String) row[0], (Long) row[1]);
        }
        stats.setComplaintsByCategory(categoryMap);

        // Status breakdown
        Map<String, Long> statusMap = new HashMap<>();
        List<Object[]> statusResults = complaintRepository.countComplaintsByStatus();
        for (Object[] row : statusResults) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setComplaintsByStatus(statusMap);

        // Location breakdown
        Map<String, Long> locMap = new HashMap<>();
        List<Object[]> locResults = complaintRepository.countComplaintsByLocation();
        for (Object[] row : locResults) {
            locMap.put((String) row[0], (Long) row[1]);
        }
        stats.setComplaintsByLocation(locMap);

        // Recent 5 complaints
        List<ComplaintResponse> recent = complaintRepository.findAllByOrderByCreatedAtDesc().stream()
                .limit(5)
                .map(ComplaintResponse::new)
                .collect(Collectors.toList());
        stats.setRecentComplaints(recent);

        return stats;
    }
}
