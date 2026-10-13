package com.campusfix.service;

import com.campusfix.dto.ComplaintCreateRequest;
import com.campusfix.dto.ComplaintResponse;
import com.campusfix.model.*;
import com.campusfix.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ComplaintTimelineRepository timelineRepository;

    @Mock
    private ComplaintUpvoteRepository upvoteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private ComplaintService complaintService;

    private User student;
    private Category category;

    @BeforeEach
    void setUp() {
        student = new User("Rahul Verma", "rahul.verma@campusfix.edu", "hashed", Role.STUDENT, "CSE", "+91 9999999999", null);
        student.setId(10L);

        category = new Category("Electrical & Power", "Fans, lighting, switches", 12, "Zap", "fan,light");
        category.setId(1L);
    }

    @Test
    @DisplayName("Should create complaint and generate formatted ticket number")
    void testCreateComplaint_Success() {
        ComplaintCreateRequest request = new ComplaintCreateRequest();
        request.setTitle("Fan regulator broken");
        request.setDescription("The speed regulator in room 201 does not work.");
        request.setCategoryId(1L);
        request.setPriority(Priority.MEDIUM);
        request.setLocationBlock("Academic Block A");
        request.setRoomNumber("Room 201");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(complaintRepository.count()).thenReturn(5L);

        Complaint savedComplaint = new Complaint();
        savedComplaint.setId(100L);
        savedComplaint.setTicketNumber("CF-2026-0006");
        savedComplaint.setTitle(request.getTitle());
        savedComplaint.setDescription(request.getDescription());
        savedComplaint.setCategory(category);
        savedComplaint.setPriority(Priority.MEDIUM);
        savedComplaint.setStatus(Status.SUBMITTED);
        savedComplaint.setLocationBlock("Academic Block A");
        savedComplaint.setRoomNumber("Room 201");
        savedComplaint.setComplainant(student);
        savedComplaint.setUpvotesCount(0);

        when(complaintRepository.save(any(Complaint.class))).thenReturn(savedComplaint);

        ComplaintResponse response = complaintService.createComplaint(request, student);

        assertNotNull(response);
        assertEquals("CF-2026-0006", response.getTicketNumber());
        assertEquals(Status.SUBMITTED, response.getStatus());
        assertEquals("Electrical & Power", response.getCategory().getName());

        verify(complaintRepository, times(1)).save(any(Complaint.class));
        verify(timelineRepository, atLeastOnce()).save(any(ComplaintTimeline.class));
    }

    @Test
    @DisplayName("Should toggle upvote on complaint and increment count")
    void testToggleUpvote_Increment() {
        Complaint complaint = new Complaint();
        complaint.setId(100L);
        complaint.setUpvotesCount(2);

        when(complaintRepository.findById(100L)).thenReturn(Optional.of(complaint));
        when(upvoteRepository.findByComplaintIdAndUserId(100L, student.getId())).thenReturn(Optional.empty());

        boolean upvoted = complaintService.toggleUpvote(100L, student);

        assertTrue(upvoted);
        assertEquals(3, complaint.getUpvotesCount());
        verify(upvoteRepository, times(1)).save(any(ComplaintUpvote.class));
    }
}
