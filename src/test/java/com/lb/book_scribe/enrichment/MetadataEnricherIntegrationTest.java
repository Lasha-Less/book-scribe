package com.lb.book_scribe.enrichment;

import com.lb.book_scribe.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class MetadataEnricherIntegrationTest {

    @Autowired
    MetadataEnricher enricher;

    @Autowired
    private GeminiEnrichmentService geminiService;

    @Test
    void testEnrichmentCombinesMongoAndScrapedData() {
        // Disable actual enrichment logic
        doNothing().when(geminiService).enrichMetadata(any());

        MongoBookDTO mongo = new MongoBookDTO();
        mongo.setTitle("The Origin of Species");
        mongo.setAuthors(List.of("Charles Darwin"));
        mongo.setPublisher("John Murray");

        ScrapedDataDTO scraped = new ScrapedDataDTO();
        scraped.setEditors(List.of("Alfred Wallace"));

        EnrichedBookDTO result = enricher.enrich(mongo, scraped);

        assertEquals("The Origin of Species", result.getTitle());
        assertEquals("John Murray", result.getPublisher());

        assertFalse(result.getAuthors().isEmpty());
        assertEquals("Darwin", result.getAuthors().get(0).getLastName());

        assertFalse(result.getEditors().isEmpty());
        assertEquals("Wallace", result.getEditors().get(0).getLastName());
    }

    @TestConfiguration
    static class MockGeminiConfig {
        @Bean
        public GeminiEnrichmentService geminiEnrichmentService() {
            return mock(GeminiEnrichmentService.class);
        }
    }

}
