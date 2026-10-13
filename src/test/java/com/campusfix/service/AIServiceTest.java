package com.campusfix.service;

import com.campusfix.dto.AICategorizationResponse;
import com.campusfix.model.Category;
import com.campusfix.model.Priority;
import com.campusfix.repository.CategoryRepository;
import com.campusfix.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AIServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ComplaintRepository complaintRepository;

    private AIService aiService;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        aiService = new AIService(categoryRepository, complaintRepository, builder);

        Category electrical = new Category("Electrical & Power", "Fans, lighting, switches, power", 12, "Zap", "fan,light,ac,electricity,spark,wire");
        electrical.setId(1L);

        Category plumbing = new Category("Plumbing & Water", "Pipe leaks, washroom water supply", 18, "Droplets", "water,leak,pipe,washroom,tap");
        plumbing.setId(2L);

        Category it = new Category("IT & Campus Network", "WiFi, lab computers, network", 24, "Wifi", "wifi,internet,lan,router");
        it.setId(3L);

        when(categoryRepository.findAll()).thenReturn(List.of(electrical, plumbing, it));
    }

    @Test
    @DisplayName("Should categorize electrical spark issue as Electrical & Power with CRITICAL priority")
    void testCategorizeComplaint_ElectricalCritical() {
        String title = "Severe sparks in switchboard";
        String description = "The fan switch is emitting continuous spark and smoke in class 102.";

        AICategorizationResponse response = aiService.categorizeComplaint(title, description);

        assertNotNull(response);
        assertEquals("Electrical & Power", response.getCategoryName());
        assertEquals(Priority.CRITICAL, response.getSuggestedPriority());
        assertTrue(response.getConfidenceScore() >= 0.70);
    }

    @Test
    @DisplayName("Should categorize plumbing water leak accurately")
    void testCategorizeComplaint_Plumbing() {
        String title = "Water pipe leakage";
        String description = "Washroom tap is overflowing continuously and water is leaking onto the corridor.";

        AICategorizationResponse response = aiService.categorizeComplaint(title, description);

        assertNotNull(response);
        assertEquals("Plumbing & Water", response.getCategoryName());
        assertEquals(18, response.getSlaHours());
    }
}
