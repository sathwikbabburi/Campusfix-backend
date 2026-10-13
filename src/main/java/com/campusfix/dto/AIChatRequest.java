package com.campusfix.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class AIChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private List<ChatMessageDto> conversationHistory;

    public AIChatRequest() {}

    public AIChatRequest(String message, List<ChatMessageDto> conversationHistory) {
        this.message = message;
        this.conversationHistory = conversationHistory;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<ChatMessageDto> getConversationHistory() { return conversationHistory; }
    public void setConversationHistory(List<ChatMessageDto> conversationHistory) { this.conversationHistory = conversationHistory; }

    public static class ChatMessageDto {
        private String sender; // 'user' or 'assistant'
        private String text;

        public ChatMessageDto() {}

        public ChatMessageDto(String sender, String text) {
            this.sender = sender;
            this.text = text;
        }

        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
