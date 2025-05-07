//package com.lb.book_scribe.enrichment;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.lb.book_scribe.dto.EnrichedBookDTO;
//import com.theokanning.openai.completion.CompletionRequest;
//import org.springframework.stereotype.Service;
//import com.theokanning.openai.service.OpenAiService;
//import org.springframework.beans.factory.annotation.Value;
//import com.lb.book_scribe.dto.PersonRoleInputDTO;
//
//
//
//
//import java.time.Duration;
//import java.util.Objects;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//public class GptEnrichmentService {
//
//    private final OpenAiService openAiService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public GptEnrichmentService(@Value("${openai.api.key}") String apiKey) {
//        System.out.println("[CONFIG] OpenAI API Key begins with: " +
//                (apiKey != null ? apiKey.substring(0, 6) + "..." : "null"));
//        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
//    }
//
//    public EnrichedBookDTO enrich(EnrichedBookDTO input) {
//        try {
//            String inputJson = objectMapper.writeValueAsString(input);
//
//            CompletionRequest request = CompletionRequest.builder()
//                    .model("gpt-4")
//                    .prompt(buildPrompt(inputJson))
//                    .maxTokens(1000)
//                    .temperature(0.4)
//                    .build();
//
//            String response = openAiService.createCompletion(request)
//                    .getChoices().get(0).getText()
//                    .trim();
//
//            EnrichedBookDTO enriched = objectMapper.readValue(response, EnrichedBookDTO.class);
//
//            // ✅ Now detect original language and set it on final object
//            String detectedLang = detectOriginalLanguage(enriched);
//            System.out.println("[GPT] Detected originalLanguage: " + detectedLang);
//            enriched.setOriginalLanguage(detectedLang);
//
//            return enriched;
//
//        } catch (Exception e) {
//            System.out.println("[GPT Error] Enrichment failed:");
//            e.printStackTrace();
//            return input;
//        }
//    }
//
//    private String buildPrompt(String inputJson) {
//        return """
//            Given the following JSON representation of a book, improve it by:
//            - Normalizing author/editor names
//            - Inferring missing fields (e.g., language, categories)
//            - Cleaning formatting
//            Return the result as JSON, matching the same structure.
//
//            Book:
//            """ + inputJson;
//    }
//
//    public String detectOriginalLanguage(EnrichedBookDTO dto) {
//        try {
//            String prompt = buildOriginalLanguagePrompt(dto);
//            System.out.println("[GPT Prompt]\n" + prompt);
//
//            CompletionRequest request = CompletionRequest.builder()
//                    .model("gpt-3.5-turbo")
//                    .prompt(prompt)
//                    .maxTokens(32)
//                    .temperature(0.2)
//                    .build();
//
//            var completion = openAiService.createCompletion(request);
//
//            if (completion.getChoices().isEmpty()) {
//                System.out.println("[GPT] No choices returned!");
//                return "unknown";
//            }
//
//            String response = completion.getChoices().get(0).getText()
//                    .trim()
//                    .toLowerCase();
//
//            System.out.println("[GPT Raw Response] " + response);
//
//            if (response.matches("^[a-z]{2}$") || response.equals("unknown")) {
//                return response;
//            } else {
//                System.out.println("[GPT] Unrecognized response format. Defaulting to 'unknown'. Response: " + response);
//                return "unknown";
//            }
//
//        } catch (Exception e) {
//            System.out.println("[GPT Error] Failed to detect original language:");
//            e.printStackTrace();
//            return "unknown";
//        }
//    }
//
//
//    private String buildOriginalLanguagePrompt(EnrichedBookDTO dto) {
//        String authors = dto.getAuthors() != null
//                ? dto.getAuthors().stream().map(a -> Stream.of(
//                        a.getFirstName(),
//                                a.getPrefix(),
//                                a.getLastName())
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.joining(" ")))
//                .collect(Collectors.joining(", "))
//                : "unknown";
//
//        return """
//        You are a metadata enrichment assistant.
//
//        Given the following book metadata, determine the original language the book was written in.
//        Return your answer as a lowercase ISO 639-1 language code (e.g., "en" for English, "fr" for French).
//        If unknown, return "unknown".
//
//        Book metadata:
//        - Title: %s
//        - Author(s): %s
//        - Publisher: %s
//        - Publication Year: %s
//        - Language: %s
//        - ISBNs: %s
//
//        What is the original language of this book?
//        """.formatted(
//                safe(dto.getTitle()),
//                authors,
//                safe(dto.getPublisher()),
//                dto.getPublicationYear() != null ? dto.getPublicationYear() : "unknown",
//                safe(dto.getLanguage()),
//                dto.getIsbns() != null ? dto.getIsbns().toString() : "[]"
//        );
//    }
//
//    private String safe(String s) {
//        return s != null ? s : "unknown";
//    }
//
//
//}
