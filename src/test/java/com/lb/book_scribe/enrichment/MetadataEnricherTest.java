package com.lb.book_scribe.enrichment;

import com.lb.book_scribe.dto.EnrichedBookDTO;
import com.lb.book_scribe.dto.InterfaceInputDTO;
import com.lb.book_scribe.dto.MongoBookDTO;
import com.lb.book_scribe.dto.ScrapedDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MetadataEnricherTest {

    private MetadataEnricher enricher;
    private GeminiEnrichmentService mockGeminiService;

    @BeforeEach
    void setUp() {
        mockGeminiService = mock(GeminiEnrichmentService.class);
        enricher = new MetadataEnricher(mockGeminiService);
    }

    @Test
    void testEnrich_appliesAllSteps() {
        MongoBookDTO mongo = new MongoBookDTO();
        ScrapedDataDTO scraped = new ScrapedDataDTO();

        EnrichedBookDTO result = enricher.enrich(mongo, scraped);

        assertNotNull(result);
        verify(mockGeminiService).enrichMetadata(result);
    }
}