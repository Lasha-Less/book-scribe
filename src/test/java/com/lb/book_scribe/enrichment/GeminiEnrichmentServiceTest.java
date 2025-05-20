package com.lb.book_scribe.enrichment;

import com.lb.book_scribe.dto.EnrichedBookDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeminiEnrichmentServiceTest {

    private GeminiEnrichmentService service;

    @BeforeEach
    void setUp() throws Exception {
        service = Mockito.spy(new GeminiEnrichmentService());

        // Enable enrichment using reflection
        Field field = GeminiEnrichmentService.class.getDeclaredField("enrichmentEnabled");
        field.setAccessible(true);
        field.set(service, true);
    }

    @Test
    void testEnrichMetadata_whenEnabled_callsAllSteps() {
        EnrichedBookDTO dto = new EnrichedBookDTO();

        doReturn("Greek").when(service).detectOriginalLanguage(dto);
        doReturn(300).when(service).inferHistoricalDate(dto);
        doNothing().when(service).fixAuthorAttribution(dto);

        service.enrichMetadata(dto);

        verify(service).detectOriginalLanguage(dto);
        verify(service).inferHistoricalDate(dto);
        verify(service).fixAuthorAttribution(dto);
    }

    @Test
    void testEnrichMetadata_whenDisabled_doesNothing() throws Exception {
        // Disable enrichment using reflection
        Field field = GeminiEnrichmentService.class.getDeclaredField("enrichmentEnabled");
        field.setAccessible(true);
        field.set(service, false);

        EnrichedBookDTO dto = new EnrichedBookDTO();
        service.enrichMetadata(dto);

        verify(service, never()).detectOriginalLanguage(any());
        verify(service, never()).inferHistoricalDate(any());
        verify(service, never()).fixAuthorAttribution(any());
    }
}