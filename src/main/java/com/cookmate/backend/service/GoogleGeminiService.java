package com.cookmate.backend.service;

import com.cookmate.backend.dto.IngredientSubstitutionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleGeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final boolean enabled;

    public GoogleGeminiService(
            @Value("${google.ai.api.key:}") String apiKey,
            @Value("${google.ai.enabled:false}") boolean enabled) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.enabled = enabled;
    }

    /**
     * Get AI-powered ingredient substitutions using Google Gemini API
     */
    public List<IngredientSubstitutionDto.Substitution> getAISubstitutions(String ingredient) {
        if (!enabled || apiKey == null || apiKey.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String url = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=%s",
                    apiKey
            );

            String prompt = String.format(
                    "Suggest 3-5 ingredient substitutions for '%s'. " +
                    "For each substitution, provide:\n" +
                    "1. The substitute ingredient name\n" +
                    "2. The ratio or amount (e.g., '1:1', '3/4 cup', '1 tbsp')\n" +
                    "3. A brief note about usage or flavor changes\n\n" +
                    "Format your response as a simple list, one per line, with format: " +
                    "SUBSTITUTE|RATIO|NOTE\n\n" +
                    "Example format:\n" +
                    "Olive Oil|3/4|For baking, reduce liquid by 3 tbsp per cup\n" +
                    "Coconut Oil|1:1|Best for baking and sautéing",
                    ingredient
            );

            GeminiRequest request = new GeminiRequest();
            GeminiRequest.Content content = new GeminiRequest.Content();
            GeminiRequest.Part part = new GeminiRequest.Part();
            part.text = prompt;
            content.parts = List.of(part);
            request.contents = List.of(content);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseGeminiResponse(response.getBody(), ingredient);
            }

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Parse Gemini API response and convert to Substitution objects
     */
    private List<IngredientSubstitutionDto.Substitution> parseGeminiResponse(
            GeminiResponse response, String originalIngredient) {
        List<IngredientSubstitutionDto.Substitution> substitutions = new ArrayList<>();

        if (response.candidates == null || response.candidates.isEmpty()) {
            return substitutions;
        }

        GeminiResponse.Candidate candidate = response.candidates.get(0);
        if (candidate.content == null || candidate.content.parts == null) {
            return substitutions;
        }

        String text = candidate.content.parts.stream()
                .filter(p -> p.text != null)
                .map(p -> p.text)
                .findFirst()
                .orElse("");

        // Parse the response text - look for lines with | separator
        String[] lines = text.split("\n");
        Pattern pattern = Pattern.compile("([^|]+)\\|([^|]+)\\|(.+)");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String substitute = matcher.group(1).trim();
                String ratio = matcher.group(2).trim();
                String note = matcher.group(3).trim();

                substitutions.add(new IngredientSubstitutionDto.Substitution(
                        substitute,
                        ratio,
                        note
                ));
            } else if (line.length() > 10) {
                // Fallback: if no pipe format, try to extract ingredient name from first part
                String[] parts = line.split("[,\\-:]", 3);
                if (parts.length >= 1) {
                    String substitute = parts[0].trim();
                    String ratio = parts.length >= 2 ? parts[1].trim() : "1:1";
                    String note = parts.length >= 3 ? parts[2].trim() : "AI-suggested substitution";

                    substitutions.add(new IngredientSubstitutionDto.Substitution(
                            substitute,
                            ratio,
                            note
                    ));
                }
            }
        }

        return substitutions;
    }

    // Request/Response DTOs for Gemini API

    @Data
    @NoArgsConstructor
    private static class GeminiRequest {
        private List<Content> contents;

        @Data
        @NoArgsConstructor
        private static class Content {
            private List<Part> parts;
        }

        @Data
        @NoArgsConstructor
        private static class Part {
            private String text;
        }
    }

    @Data
    @NoArgsConstructor
    private static class GeminiResponse {
        private List<Candidate> candidates;

        @Data
        @NoArgsConstructor
        private static class Candidate {
            private Content content;

            @Data
            @NoArgsConstructor
            private static class Content {
                private List<Part> parts;

                @Data
                @NoArgsConstructor
                private static class Part {
                    private String text;
                }
            }
        }
    }
}

