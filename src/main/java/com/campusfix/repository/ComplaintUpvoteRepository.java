package com.campusfix.repository;

import com.campusfix.model.ComplaintUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintUpvoteRepository extends JpaRepository<ComplaintUpvote, Long> {
    Optional<ComplaintUpvote> findByComplaintIdAndUserId(Long complaintId, Long userId);
    Boolean existsByComplaintIdAndUserId(Long complaintId, Long userId);
    Long countByComplaintId(Long complaintId);
}
