package com.lb.book_scribe.enrichment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.book_scribe.dto.EnrichedBookDTO;
import com.lb.book_scribe.dto.PersonRoleInputDTO;
import com.lb.book_scribe.util.PersonNameParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class GeminiEnrichmentService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    @Value("${ai.enrichment.enabled:true}")
    private boolean enrichmentEnabled;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void enrichMetadata(EnrichedBookDTO dto) {
        if (!enrichmentEnabled) return;

        dto.setOriginalLanguage(detectOriginalLanguage(dto));
        dto.setHistoricalDate(inferHistoricalDate(dto));
        List<PersonRoleInputDTO> enriched = enrichContributors(dto);
        dto.getOthers().addAll(enriched);
        fixAuthorAttribution(dto);

    }

    public void fixAuthorAttribution(EnrichedBookDTO dto) {
        if (!enrichmentEnabled) {
            log.info("AI enrichment disabled via config");
            return;
        }

        if (dto.getAuthors() == null || dto.getAuthors().isEmpty()) return;

        String authors = dto.getAuthors().stream()
                .map(PersonRoleInputDTO::getFullName)
                .collect(Collectors.joining(", "));

        String editors = dto.getEditors() != null
                ? dto.getEditors().stream().map(PersonRoleInputDTO::getFullName).collect(Collectors.joining(", "))
                : "none";

        String prompt = """
        You are a metadata enrichment assistant.

        A book has the following listed contributors:
        - Authors: %s
        - Editors: %s

        Determine whether the people listed as authors are **actually** the original authors of the book, 
        or if they were likely acting as editors/compilers (e.g., in anthologies, translations, etc.).

        Return a single word response:
        - "keep" → if they are actual authors
        - "move" → if they should be editors instead
        """.formatted(authors, editors);

        try {
            log.info("Prompt to Gemini for author attribution:\n{}", prompt);
            String response = callGemini(prompt).block();
            log.info("Gemini author attribution response: {}", response);

            String answer = extractPlainTextFromGeminiResponse(response).trim().toLowerCase();

            if ("move".equals(answer)) {
                log.info("Gemini recommends moving authors to editors.");
                if (dto.getEditors() == null) {
                    dto.setEditors(new ArrayList<>());
                }
                List<PersonRoleInputDTO> moved = dto.getAuthors().stream()
                        .map(a -> new PersonRoleInputDTO(
                                a.getFirstName(),
                                a.getPrefix(),
                                a.getLastName(),
                                "Editor"))
                        .toList();

                dto.getEditors().addAll(moved);
                dto.setAuthors(new ArrayList<>());

                // 🔧 Add this here: deduplicate editors after merging
                Set<String> seen = new HashSet<>();
                List<PersonRoleInputDTO> uniqueEditors = dto.getEditors().stream()
                        .filter(p -> seen.add(p.getFullName() + "::" + safe(p.getRole())))
                        .toList();
                dto.setEditors(uniqueEditors);
            }
            else {
                log.info("Gemini recommends keeping authors as-is.");
            }

        } catch (Exception e) {
            log.error("Failed to fix author attribution", e);
        }
    }


    public List<PersonRoleInputDTO> enrichContributors(EnrichedBookDTO dto) {
        if (!enrichmentEnabled) {
            log.info("AI enrichment disabled via config");
            return dto.getOthers();
        }

        String prompt = """
        You are a metadata enrichment assistant.

        Based on the following book description text, identify any people who contributed to the book, such as:
        translators, illustrators, commentators, foreword writers, or essay contributors.

        Do not include the main author. Focus only on additional contributors mentioned in the description.

        Return each contributor as a JSON object with:
        - "name": full name as a single string
        - "role": specific contribution (e.g., "Translator", "Illustrator")

        Return only a JSON array like this:
        [
          {"name": "Jane Smith", "role": "Foreword Writer"},
          {"name": "John Doe", "role": "Contributor"}
        ]

        Description:
        %s
        """.formatted(safe(dto.getDescription()));

        try {
            log.info("Prompt to Gemini for contributor enrichment from description:\n{}", prompt);
            String response = callGemini(prompt).block();
            log.info("Gemini contributor response: {}", response);

            String rawJson = extractPlainTextFromGeminiResponse(response);

            List<Map<String, String>> rawList = objectMapper.readValue(
                    rawJson, new TypeReference<>() {}
            );

            log.info("Parsed contributors before filtering: {}", rawList);

            Set<String> existingNameRoles = Stream.of(
                            dto.getAuthors(), dto.getEditors(), dto.getOthers()
                    ).filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .map(p -> p.getFullName() + "::" + safe(p.getRole()))
                    .collect(Collectors.toSet());

            return rawList.stream()
                    .map(entry -> PersonNameParser.parse(entry.get("name"), entry.get("role")))
                    .filter(p -> !existingNameRoles.contains(p.getFullName() + "::" + safe(p.getRole())))
                    .toList();

        } catch (Exception e) {
            log.error("Failed to enrich contributors from description", e);
            return dto.getOthers();
        }
    }



    public Integer inferHistoricalDate(EnrichedBookDTO dto) {
        if (!enrichmentEnabled) {
            log.info("AI enrichment disabled via config");
            return null;
        }

        String authors = dto.getAuthors() != null
                ? dto.getAuthors().stream().map(a -> Stream.of(
                                a.getFirstName(),
                                a.getPrefix(),
                                a.getLastName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(", "))
                : "unknown";

        String prompt = """
        You are a metadata enrichment assistant.

        Given the following book metadata, infer the historical date the book was originally written or first published.
        If a specific year is known, return it (e.g., "1844").
        If only a century is known (e.g., "16th century" or "4th century BCE"), convert it to a year:
        - Use the midpoint of the century: "16th century" → 1550
        - For BCE, use negative numbers: "4th century BCE" → -350

        Return the result as a 4-digit number (or negative 4-digit for BCE). If unknown, return "unknown".

        Book metadata:
        - Title: %s
        - Author(s): %s
        - Publisher: %s
        - Language: %s
        - Publication Year: %s

        What is the historical date?
        """.formatted(
                safe(dto.getTitle()),
                authors,
                safe(dto.getPublisher()),
                safe(dto.getLanguage()),
                dto.getPublicationYear() != null ? dto.getPublicationYear().toString() : "unknown"
        );

        try {
            log.info("Prompt to Gemini for historical date:\n{}", prompt);
            String response = callGemini(prompt).block();
            log.info("Gemini historical date response: {}", response);
            String answer = extractPlainTextFromGeminiResponse(response).toLowerCase().trim();

            if (answer.matches("-?\\d{3,4}")) {
                return Integer.valueOf(answer);
            }

            return null;

        } catch (Exception e) {
            log.error("Gemini historical date detection failed", e);
            return null;
        }
    }



    public String detectOriginalLanguage(EnrichedBookDTO dto) {
        if (!enrichmentEnabled) {
            log.info("AI enrichment disabled via config");
            return "unknown";
        }

        String authors = dto.getAuthors() != null
                ? dto.getAuthors().stream().map(a -> Stream.of(
                                a.getFirstName(),
                                a.getPrefix(),
                                a.getLastName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining(", "))
                : "unknown";

        String prompt = """
        You are a metadata enrichment assistant.

        Given the following book metadata, determine the original language the book was written in.
        Return your answer as a lowercase ISO 639-1 language code (e.g., "en" for English, "fr" for French).
        If unknown, return "unknown".

        Book metadata:
        - Title: %s
        - Author(s): %s
        - Publisher: %s
        - Publication Year: %s
        - Language: %s
        - ISBNs: %s

        What is the original language of this book?
        """.formatted(
                safe(dto.getTitle()),
                authors,
                safe(dto.getPublisher()),
                dto.getPublicationYear() != null ? dto.getPublicationYear() : "unknown",
                safe(dto.getLanguage()),
                dto.getIsbns() != null ? dto.getIsbns().toString() : "[]"
        );

        try {
            log.info("Prompt to Gemini:\n{}", prompt);
            String response = callGemini(prompt).block();
            log.info("Gemini raw response: {}", response);
            return extractPlainTextFromGeminiResponse(response).toLowerCase().trim();
        } catch (Exception e) {
            log.error("Gemini language detection failed", e);
            return "unknown";
        }
    }

    private String safe(String val) {
        return val != null ? val : "unknown";
    }

    private Mono<String> callGemini(String promptJson) {
        Map<String, Object> payload = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", promptJson))))
        );

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Gemini API call failed: {}", error.getMessage()));
    }

    private String extractPlainTextFromGeminiResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String rawText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Extract clean JSON array from markdown block if present
            if (rawText.contains("```json")) {
                int start = rawText.indexOf("```json") + 7;
                int end = rawText.indexOf("```", start);
                return rawText.substring(start, end).trim();
            }

            // Fallback: try first [ and last ]
            int start = rawText.indexOf('[');
            int end = rawText.lastIndexOf(']');
            if (start >= 0 && end > start) {
                return rawText.substring(start, end + 1).trim();
            }

            return rawText.trim();

        } catch (Exception e) {
            log.error("Failed to extract plain text from Gemini response", e);
            return "[]";
        }
    }

}
