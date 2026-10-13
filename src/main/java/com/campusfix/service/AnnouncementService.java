package com.campusfix.service;

import com.campusfix.dto.AnnouncementRequest;
import com.campusfix.exception.ResourceNotFoundException;
import com.campusfix.model.Announcement;
import com.campusfix.model.User;
import com.campusfix.repository.AnnouncementRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Cacheable(value = "activeAnnouncements")
    @Transactional(readOnly = true)
    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    @CacheEvict(value = "activeAnnouncements", allEntries = true)
    @Transactional
    public Announcement createAnnouncement(AnnouncementRequest request, User admin) {
        Announcement announcement = new Announcement(
                request.getTitle(),
                request.getContent(),
                request.getDepartment(),
                request.getPriority(),
                admin
        );
        return announcementRepository.save(announcement);
    }

    @CacheEvict(value = "activeAnnouncements", allEntries = true)
    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcementRepository.delete(announcement);
    }
}
