package com.email_writer_sb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;
    private final String apiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder,
                                 @Value("${gemini.api.url}") String baseUrl,
                                 @Value("${gemini.api.key}") String geminiKey) {
        this.apiKey = geminiKey;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {
        String prompt = buildPrompt(emailRequest);

        String requestBody = String.format("""
                {
                    "contents": [
                      {
                        "parts": [
                          {
                            "text": "%s"
                          }
                        ]
                      }
                    ]
                }
                """, prompt.replace("\"", "\\\""));

        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.5-flash:generateContent")
                            .build())
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return cleanResponse(extractResponseContent(response));

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RuntimeException("Rate limit hit! Thodi der baad retry karo. (429)");
            } else if (e.getStatusCode().is5xxServerError()) {
                throw new RuntimeException("Gemini server error! Baad mein try karo. (" + e.getStatusCode() + ")");
            } else {
                throw new RuntimeException("API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract response: " + e.getMessage());
        }
    }

    private String cleanResponse(String response) {
        if (response == null) return "";

        response = response.replaceAll("(?i)^(subject|re|fw|fwd):.*\\n", "");
        response = response.replaceAll("(?i)^(here is|certainly|sure|of course|absolutely)[^\\n]*\\n", "");
        response = response.replaceAll("\\n{3,}", "\n\n");

        return response.trim();
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("""
            You are an elite professional email writer used by Fortune 500 executives.
            Your replies are known for being sharp, human, and effective.
            
            YOUR MISSION:
            Read the original email carefully and craft a reply that:
            - Directly addresses every point raised
            - Sounds human and natural, NOT robotic or templated
            - Is confident, clear, and appropriately warm
            - Gets to the point without unnecessary filler phrases
            
            ═══════════════════════════════════
            ABSOLUTE OUTPUT RULES (NEVER BREAK):
            ═══════════════════════════════════
            ✗ NO subject line — not even "Re:" or "Subject:"
            ✗ NO preamble like "Here is your reply:" or "Certainly!"
            ✗ NO markdown — no **bold**, no bullet points, no headers
            ✗ NO placeholder text like [Your Name] or [Company]
            ✗ NO repetition of the original email content
            ✗ NO filler phrases like "I hope this email finds you well"
            
            ✓ START directly with the greeting (Dear X, / Hi X, / Hello X,)
            ✓ BODY should be 2-4 focused paragraphs
            ✓ END with a warm sign-off: Best regards, / Warm regards, / Sincerely,
            ✓ After sign-off, write exactly: [Your Name]
            ✓ Each paragraph must serve a distinct purpose
            ✓ Mirror the formality level of the original email
            
            """);

        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("TONE INSTRUCTION:\n");
            prompt.append("Write this reply in a ")
                    .append(emailRequest.getTone().toUpperCase())
                    .append(" tone.\n");

            switch (emailRequest.getTone().toLowerCase()) {
                case "professional" -> prompt.append("→ Polished, structured, business-appropriate. Confident but not cold.\n");
                case "formal"       -> prompt.append("→ Respectful and measured. Suitable for senior executives or legal matters.\n");
                case "friendly"     -> prompt.append("→ Warm and approachable. Conversational but still professional.\n");
                case "apologetic"   -> prompt.append("→ Empathetic and sincere. Acknowledge the issue, take responsibility, offer a path forward.\n");
                case "assertive"    -> prompt.append("→ Direct and confident. State your position clearly without being aggressive.\n");
                case "concise"      -> prompt.append("→ Maximum 2 short paragraphs. Every word must earn its place. No fluff.\n");
            }
            prompt.append("\n");
        }

        prompt.append("""
            ═══════════════════════════════
            ORIGINAL EMAIL (read carefully):
            ═══════════════════════════════
            """);
        prompt.append(emailRequest.getEmailContent().trim());
        prompt.append("""
            
            ═══════════════════════════════
            
            Now write the reply body. Begin with the greeting. Output nothing else.
            """);

        return prompt.toString();
    }
}
