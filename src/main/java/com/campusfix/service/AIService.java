package com.campusfix.service;

import com.campusfix.dto.AICategorizationResponse;
import com.campusfix.dto.AIChatRequest;
import com.campusfix.dto.AIChatResponse;
import com.campusfix.model.Category;
import com.campusfix.model.Complaint;
import com.campusfix.model.Priority;
import com.campusfix.repository.CategoryRepository;
import com.campusfix.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private final CategoryRepository categoryRepository;
    private final ComplaintRepository complaintRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    public AIService(CategoryRepository categoryRepository,
                     ComplaintRepository complaintRepository,
                     RestTemplateBuilder restTemplateBuilder) {
        this.categoryRepository = categoryRepository;
        this.complaintRepository = complaintRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(4))
                .setReadTimeout(Duration.ofSeconds(6))
                .build();
    }

    /**
     * Automatic Complaint Categorization & Routing Engine (CampusFix Core AI)
     * Analyzes title and description to determine optimal Category, Priority, and Routing.
     */
    public AICategorizationResponse categorizeComplaint(String title, String description) {
        String fullText = ((title != null ? title : "") + " " + (description != null ? description : "")).toLowerCase();
        List<Category> categories = categoryRepository.findAll();

        Category bestCategory = null;
        int maxMatches = 0;
        List<String> matchedTokens = new ArrayList<>();

        // 1. Semantic keyword & token evaluation
        for (Category cat : categories) {
            if (cat.getKeywords() == null || cat.getKeywords().isBlank()) continue;
            String[] keywords = cat.getKeywords().split(",");
            int matches = 0;
            List<String> currentMatches = new ArrayList<>();

            for (String kw : keywords) {
                String cleanKw = kw.trim().toLowerCase();
                if (!cleanKw.isEmpty() && fullText.contains(cleanKw)) {
                    matches += 1;
                    currentMatches.add(cleanKw);
                }
            }

            // Category name check
            if (fullText.contains(cat.getName().toLowerCase())) {
                matches += 3;
                currentMatches.add(cat.getName());
            }

            if (matches > maxMatches) {
                maxMatches = matches;
                bestCategory = cat;
                matchedTokens = currentMatches;
            }
        }

        // Default to General / First category if no matches found
        if (bestCategory == null && !categories.isEmpty()) {
            bestCategory = categories.get(0);
        }

        // 2. Urgent Priority Detection
        Priority priority = Priority.MEDIUM;
        String priorityReason = "Normal priority based on issue description.";

        Pattern criticalPattern = Pattern.compile("\\b(fire|spark|sparking|shock|smoke|explosion|burst|blast|emergency|hazard|electrocution|short circuit|flooding)\\b");
        Pattern highPattern = Pattern.compile("\\b(urgent|immediate|broken|blackout|leakage|overflow|stolen|theft|unsafe|danger|fell down)\\b");

        if (criticalPattern.matcher(fullText).find()) {
            priority = Priority.CRITICAL;
            priorityReason = "High safety hazard / emergency signals detected.";
        } else if (highPattern.matcher(fullText).find()) {
            priority = Priority.HIGH;
            priorityReason = "Significant disruption / urgent repair requested.";
        } else if (fullText.contains("minor") || fullText.contains("small") || fullText.contains("dust")) {
            priority = Priority.LOW;
            priorityReason = "Routine or non-critical issue.";
        }

        double confidence = maxMatches > 0 ? Math.min(0.98, 0.65 + (maxMatches * 0.08)) : 0.50;
        String department = bestCategory != null ? bestCategory.getName() + " Dept" : "General Facilities";
        String explanation = String.format("Auto-classified under '%s' (%s). %s (Confidence: %.0f%%)",
                bestCategory != null ? bestCategory.getName() : "General",
                priorityReason,
                matchedTokens.isEmpty() ? "Standard routing applied." : "Matched keywords: " + String.join(", ", matchedTokens),
                confidence * 100);

        return new AICategorizationResponse(
                bestCategory != null ? bestCategory.getId() : 1L,
                bestCategory != null ? bestCategory.getName() : "General",
                priority,
                department,
                bestCategory != null ? bestCategory.getSlaHours() : 24,
                Math.round(confidence * 100.0) / 100.0,
                explanation,
                String.join(", ", matchedTokens)
        );
    }

    /**
     * RAG (Retrieval-Augmented Generation) Chatbot Engine
     * Retrieves campus complaint database facts and constructs context-aware answers.
     */
    public AIChatResponse processChat(AIChatRequest request) {
        String userQuery = request.getMessage().trim();
        String lowerQuery = userQuery.toLowerCase();

        // Step 1: RAG Retrieval - Search for Ticket Number in user prompt (e.g. CF-2026-001)
        Pattern ticketPattern = Pattern.compile("(?i)CF-\\d{4}-\\d{3,4}");
        Matcher matcher = ticketPattern.matcher(userQuery);

        List<Complaint> retrievedComplaints = new ArrayList<>();
        if (matcher.find()) {
            String foundTicket = matcher.group();
            complaintRepository.findByTicketNumber(foundTicket.toUpperCase()).ifPresent(retrievedComplaints::add);
        } else if (lowerQuery.contains("ticket") || lowerQuery.contains("status") || lowerQuery.contains("complaint")) {
            // Retrieve recent 3 complaints as context
            retrievedComplaints = complaintRepository.findAllByOrderByCreatedAtDesc().stream()
                    .limit(3)
                    .collect(Collectors.toList());
        }

        // Step 2: Context Injection & Knowledge Assembly
        List<Category> allCategories = categoryRepository.findAll();
        StringBuilder ragContext = new StringBuilder();
        ragContext.append("Campus Maintenance Knowledge Base:\n");
        for (Category cat : allCategories) {
            ragContext.append(String.format("- %s: SLA %d hours. Keywords: %s\n", cat.getName(), cat.getSlaHours(), cat.getKeywords()));
        }

        if (!retrievedComplaints.isEmpty()) {
            ragContext.append("\nRetrieved Database Complaints:\n");
            for (Complaint c : retrievedComplaints) {
                ragContext.append(String.format("Ticket %s: '%s' | Category: %s | Status: %s | Priority: %s | Block: %s | Staff: %s\n",
                        c.getTicketNumber(), c.getTitle(), c.getCategory().getName(), c.getStatus(), c.getPriority(),
                        c.getLocationBlock(), c.getAssignedStaff() != null ? c.getAssignedStaff().getFullName() : "Unassigned"));
            }
        }

        // Step 3: LLM Generation (External Gemini/OpenAI API or Local RAG Inference Engine)
        String replyText;
        List<String> suggestedQuestions = new ArrayList<>();
        String categorySuggestion = null;

        // Try external Gemini if key is provided
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            try {
                replyText = callGeminiApi(userQuery, ragContext.toString());
            } catch (Exception e) {
                log.warn("Gemini API call failed, falling back to internal RAG engine: {}", e.getMessage());
                replyText = generateInternalRAGResponse(userQuery, retrievedComplaints, allCategories);
            }
        } else {
            // High-precision local RAG conversational generator
            replyText = generateInternalRAGResponse(userQuery, retrievedComplaints, allCategories);
        }

        // Suggested follow-up questions for interactive UX
        if (!retrievedComplaints.isEmpty()) {
            Complaint primary = retrievedComplaints.get(0);
            suggestedQuestions.add("What is the resolution timeline for " + primary.getTicketNumber() + "?");
            suggestedQuestions.add("Who is assigned to ticket " + primary.getTicketNumber() + "?");
        } else {
            suggestedQuestions.add("How do I report a campus water leak?");
            suggestedQuestions.add("What is the average SLA for WiFi issues?");
            suggestedQuestions.add("How does automated complaint routing work?");
        }

        // Build summary list for card rendering
        List<AIChatResponse.ComplaintSummaryDto> summaryList = retrievedComplaints.stream()
                .map(c -> new AIChatResponse.ComplaintSummaryDto(c.getTicketNumber(), c.getTitle(), c.getStatus().name()))
                .collect(Collectors.toList());

        return new AIChatResponse(replyText, suggestedQuestions, summaryList, categorySuggestion);
    }

    private String generateInternalRAGResponse(String query, List<Complaint> complaints, List<Category> categories) {
        String q = query.toLowerCase();

        // 1. Specific ticket lookup response
        if (!complaints.isEmpty() && (q.contains("cf-") || q.contains("status") || q.contains("ticket"))) {
            Complaint c = complaints.get(0);
            return String.format(
                    "### 🎫 Complaint Status for **%s**\n\n" +
                    "- **Title**: %s\n" +
                    "- **Current Status**: `%s`\n" +
                    "- **Category**: %s\n" +
                    "- **Location**: %s (%s)\n" +
                    "- **Priority**: %s\n" +
                    "- **Assigned Staff**: %s\n" +
                    "- **SLA Window**: %d Hours\n\n" +
                    "%s" +
                    "💡 *You can track real-time progress on your dashboard or upvote if you are also impacted!*",
                    c.getTicketNumber(),
                    c.getTitle(),
                    c.getStatus(),
                    c.getCategory().getName(),
                    c.getLocationBlock(),
                    c.getRoomNumber() != null ? c.getRoomNumber() : "General area",
                    c.getPriority(),
                    c.getAssignedStaff() != null ? c.getAssignedStaff().getFullName() + " (" + c.getAssignedStaff().getDepartment() + ")" : "Pending assignment",
                    c.getCategory().getSlaHours(),
                    c.getStatus().name().equals("RESOLVED") ? "✅ **Resolution Note**: " + (c.getResolutionNotes() != null ? c.getResolutionNotes() : "Issue successfully fixed.") + "\n\n" : ""
            );
        }

        // 2. SLA or category questions
        if (q.contains("sla") || q.contains("time") || q.contains("how long") || q.contains("duration")) {
            StringBuilder sb = new StringBuilder("### ⏱️ CampusFix Resolution Service Level Agreements (SLA)\n\n");
            sb.append("CampusFix enforces automated SLA routing based on category urgency:\n\n");
            for (Category cat : categories) {
                sb.append(String.format("- **%s**: Max **%d hours** turnaround\n", cat.getName(), cat.getSlaHours()));
            }
            sb.append("\n⚠️ *Critical safety or electrical hazards are escalated immediately to campus maintenance control.*");
            return sb.toString();
        }

        // 3. How to lodge a complaint
        if (q.contains("how to") || q.contains("report") || q.contains("submit") || q.contains("lodge") || q.contains("new complaint")) {
            return "### 📝 Lodging a Complaint with CampusFix\n\n" +
                   "1. Click on **'New Complaint'** in the top navigation bar.\n" +
                   "2. Fill in the **Title** and **Detailed Description**.\n" +
                   "3. 🤖 **AI Assistant Preview**: Our system automatically infers the correct maintenance department and priority in real-time.\n" +
                   "4. Enter the **Campus Block** and **Room Number**.\n" +
                   "5. Submit your ticket! You will receive a unique tracking ID (e.g., `CF-2026-XXXX`).\n\n" +
                   "✨ *Duplicate prevention*: If a classmate has already reported the issue, simply click **'Upvote'** on their ticket!";
        }

        // 4. Electrical, Plumbing, WiFi specific advice
        if (q.contains("wifi") || q.contains("internet") || q.contains("lan")) {
            return "### 📶 Campus WiFi & Network Troubleshooting\n\n" +
                   "- **Department**: IT & Campus Network (SLA: 24 Hours)\n" +
                   "- **Common Fix**: Try forgetting the network and reconnecting using your campus LDAP credentials.\n" +
                   "- If access point indicators are blinking amber/red, lodge a ticket under **IT & Campus Network** specifying the building floor and room.";
        }

        if (q.contains("water") || q.contains("leak") || q.contains("washroom")) {
            return "### 🚰 Plumbing & Water Supply Notice\n\n" +
                   "- **Department**: Plumbing & Water Maintenance (SLA: 18 Hours)\n" +
                   "- For emergency pipe bursts or overflowing overhead tanks, our automated router marks tickets as **HIGH** or **CRITICAL** priority for immediate on-site response.";
        }

        // Default intelligent assistant greeting
        return "👋 Hello! I am your **CampusFix AI Maintenance Assistant**.\n\n" +
               "I can assist you with:\n" +
               "- 🔍 **Tracking Tickets**: Provide your ticket number (e.g. `CF-2026-001`) to view current technician status.\n" +
               "- 🤖 **Automated Categorization**: Understand how issues are routed to Electrical, IT, Plumbing, or Civil departments.\n" +
               "- ⏱️ **SLA Guidelines**: Learn estimated resolution times across campus blocks.\n" +
               "- 📢 **Campus Advisories**: Check active maintenance announcements.\n\n" +
               "How can I help you today?";
    }

    private String callGeminiApi(String userPrompt, String context) {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        String systemPrompt = "You are CampusFix AI, a smart assistant for a college campus complaint resolution system. " +
                "Use the provided context to answer clearly, empathetically, and accurately in markdown.\n\nContext:\n" + context;

        Map<String, Object> textPart = Map.of("text", systemPrompt + "\n\nUser Question: " + userPrompt);
        Map<String, Object> parts = Map.of("parts", List.of(textPart));
        Map<String, Object> contents = Map.of("contents", List.of(parts));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contents, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map body = response.getBody();
            List candidates = (List) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map first = (Map) candidates.get(0);
                Map content = (Map) first.get("content");
                List partsList = (List) content.get("parts");
                Map part = (Map) partsList.get(0);
                return (String) part.get("text");
            }
        }
        return "I processed your request, but received an empty response from the AI model.";
    }
}
