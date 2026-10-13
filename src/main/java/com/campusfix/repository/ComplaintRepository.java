package com.campusfix.repository;

import com.campusfix.model.Complaint;
import com.campusfix.model.Priority;
import com.campusfix.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByTicketNumber(String ticketNumber);

    List<Complaint> findByComplainantIdOrderByCreatedAtDesc(Long complainantId);

    List<Complaint> findByAssignedStaffIdOrderByCreatedAtDesc(Long staffId);

    List<Complaint> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    List<Complaint> findByStatusOrderByCreatedAtDesc(Status status);

    List<Complaint> findAllByOrderByCreatedAtDesc();

    Long countByStatus(Status status);

    Long countByPriority(Priority priority);

    Long countByCategoryId(Long categoryId);

    @Query("SELECT c.category.name, COUNT(c) FROM Complaint c GROUP BY c.category.name")
    List<Object[]> countComplaintsByCategory();

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countComplaintsByStatus();

    @Query("SELECT c.locationBlock, COUNT(c) FROM Complaint c GROUP BY c.locationBlock ORDER BY COUNT(c) DESC")
    List<Object[]> countComplaintsByLocation();
}
