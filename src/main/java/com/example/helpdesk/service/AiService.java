package com.example.helpdesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * AI Service using Google Gemini API.
 * Provides smart categorization, priority suggestion, and reply generation.
 * 
 * @author Facility Helpdesk Team
 */
@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-2.0-flash}")
    private String model;

    private final HttpClient httpClient;

    public AiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Check if the AI service is configured and available.
     */
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * Auto-categorize a ticket based on its subject and description.
     * Returns the suggested category name.
     */
    public String suggestCategory(String subject, String description) {
        if (!isAvailable()) return null;

        String prompt = String.format(
            "You are a helpdesk ticket categorization system for a university. " +
            "Based on the ticket below, suggest ONE category from this list: " +
            "Hardware Issue, Software Issue, Facility Maintenance, Network Issue, Electrical Issue, Cleaning Request. " +
            "Respond with ONLY the category name, nothing else.\n\n" +
            "Subject: %s\nDescription: %s", subject, description);

        return callGemini(prompt);
    }

    /**
     * Suggest priority level for a ticket.
     * Returns: LOW, MEDIUM, HIGH, or URGENT.
     */
    public String suggestPriority(String subject, String description) {
        if (!isAvailable()) return null;

        String prompt = String.format(
            "You are a helpdesk priority assessment system for a university. " +
            "Based on the ticket below, suggest the priority level. " +
            "Consider: safety hazards=URGENT, broken essential equipment=HIGH, inconvenience=MEDIUM, minor requests=LOW. " +
            "Respond with ONLY one word: LOW, MEDIUM, HIGH, or URGENT.\n\n" +
            "Subject: %s\nDescription: %s", subject, description);

        return callGemini(prompt);
    }

    /**
     * Generate a smart reply suggestion for staff responding to a ticket.
     */
    public String suggestReply(String subject, String description, String status) {
        if (!isAvailable()) return null;

        String prompt = String.format(
            "You are a helpful university helpdesk staff assistant. " +
            "Generate a professional, friendly reply to the following student ticket. " +
            "The reply should acknowledge the issue, provide expected resolution timeline, and ask for any needed details. " +
            "Keep it concise (2-3 paragraphs). Write in English.\n\n" +
            "Ticket Subject: %s\nTicket Description: %s\nCurrent Status: %s", subject, description, status);

        return callGemini(prompt);
    }

    /**
     * Summarize a ticket with multiple comments.
     */
    public String summarizeTicket(String subject, String description, String comments) {
        if (!isAvailable()) return null;

        String prompt = String.format(
            "Summarize the following helpdesk ticket and its conversation in 2-3 bullet points. " +
            "Focus on: the issue, actions taken, and current status.\n\n" +
            "Subject: %s\nDescription: %s\n\nConversation:\n%s", subject, description, comments);

        return callGemini(prompt);
    }

    /**
     * Call Google Gemini API.
     */
    private String callGemini(String prompt) {
        try {
            String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey);

            // Escape special characters for JSON
            String escapedPrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String requestBody = String.format(
                "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]," +
                "\"generationConfig\":{\"temperature\":0.3,\"maxOutputTokens\":1024}}",
                escapedPrompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractTextFromResponse(response.body());
            } else {
                logger.error("Gemini API error. Status: {}, Body: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error calling Gemini API: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract text content from Gemini API JSON response.
     * Simple parsing without external JSON library dependency.
     */
    private String extractTextFromResponse(String json) {
        try {
            // Find "text" : "..." in response
            String marker = "\"text\"";
            int textIndex = json.indexOf(marker);
            if (textIndex == -1) return null;

            // Find the colon after "text"
            int colonIndex = json.indexOf(":", textIndex + marker.length());
            if (colonIndex == -1) return null;

            // Find the opening quote of the value
            int startQuote = json.indexOf("\"", colonIndex + 1);
            if (startQuote == -1) return null;

            // Find the closing quote (handling escaped quotes)
            int endQuote = startQuote + 1;
            while (endQuote < json.length()) {
                if (json.charAt(endQuote) == '\"' && json.charAt(endQuote - 1) != '\\') {
                    break;
                }
                endQuote++;
            }

            String text = json.substring(startQuote + 1, endQuote);
            // Unescape
            text = text.replace("\\n", "\n")
                       .replace("\\r", "\r")
                       .replace("\\t", "\t")
                       .replace("\\\"", "\"")
                       .replace("\\\\", "\\");

            return text.trim();
        } catch (Exception e) {
            logger.error("Error parsing Gemini response: {}", e.getMessage());
            return null;
        }
    }
}
