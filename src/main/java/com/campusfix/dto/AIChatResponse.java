package com.campusfix.dto;

import java.util.List;

public class AIChatResponse {

    private String reply;
    private List<String> suggestedQuestions;
    private List<ComplaintSummaryDto> relevantTickets;
    private String categorySuggestion;

    public AIChatResponse() {}

    public AIChatResponse(String reply, List<String> suggestedQuestions, List<ComplaintSummaryDto> relevantTickets, String categorySuggestion) {
        this.reply = reply;
        this.suggestedQuestions = suggestedQuestions;
        this.relevantTickets = relevantTickets;
        this.categorySuggestion = categorySuggestion;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public List<String> getSuggestedQuestions() { return suggestedQuestions; }
    public void setSuggestedQuestions(List<String> suggestedQuestions) { this.suggestedQuestions = suggestedQuestions; }

    public List<ComplaintSummaryDto> getRelevantTickets() { return relevantTickets; }
    public void setRelevantTickets(List<ComplaintSummaryDto> relevantTickets) { this.relevantTickets = relevantTickets; }

    public String getCategorySuggestion() { return categorySuggestion; }
    public void setCategorySuggestion(String categorySuggestion) { this.categorySuggestion = categorySuggestion; }

    public static class ComplaintSummaryDto {
        private String ticketNumber;
        private String title;
        private String status;

        public ComplaintSummaryDto() {}

        public ComplaintSummaryDto(String ticketNumber, String title, String status) {
            this.ticketNumber = ticketNumber;
            this.title = title;
            this.status = status;
        }

        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
