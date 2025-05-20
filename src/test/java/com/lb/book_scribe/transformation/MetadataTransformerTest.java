package com.lb.book_scribe.transformation;

import com.lb.book_scribe.dto.BookInputDTO;
import com.lb.book_scribe.dto.EnrichedBookDTO;
import com.lb.book_scribe.dto.PersonRoleInputDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetadataTransformerTest {

    private MetadataTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MetadataTransformer();
    }

    @Test
    void testTransform_mapsAllFieldsCorrectly() throws IllegalAccessException {
        EnrichedBookDTO dto = new EnrichedBookDTO();
        dto.setTitle("  test title  ");
        dto.setAuthors(List.of(new PersonRoleInputDTO(null, null, "Author A", "author")));
        dto.setEditors(List.of(new PersonRoleInputDTO(null, null, "Editor A", "editor")));
        dto.setLanguage("  English ");
        dto.setFormat(" Hardcover ");
        dto.setLocation(" Shelf 3 ");
        dto.setInStock(true);
        dto.setCollections(List.of("Collection A"));
        dto.setOriginalLanguage("Greek");
        dto.setPublisher("Penguin");
        dto.setPublicationYear(1999);
        dto.setHistoricalDate(400);
        dto.setOthers(List.of(new PersonRoleInputDTO(null, null, "Other A", "Note")));

        BookInputDTO result = transformer.transform(dto);

        assertEquals("test title", result.getTitle());
        assertEquals(List.of("Author A"), result.getAuthors().stream().map(PersonRoleInputDTO::getLastName).toList());
        assertEquals(List.of("Editor A"), result.getEditors().stream().map(PersonRoleInputDTO::getLastName).toList());
        assertEquals("English", result.getLanguage());
        assertEquals("Hardcover", result.getFormat());
        assertEquals("Shelf 3", result.getLocation());
        assertTrue(result.getInStock());
        assertEquals(List.of("Collection A"), result.getCollections());
        assertEquals("Greek", result.getOriginalLanguage());
        assertEquals("Penguin", result.getPublisher());
        assertEquals(1999, result.getPublicationYear());
        assertEquals(400, result.getHistoricalDate());
        assertEquals(List.of("Other A"), result.getOthers().stream().map(PersonRoleInputDTO::getLastName).toList());
    }

    @Test
    void testTransform_handleMissingValues() throws IllegalAccessException {
        EnrichedBookDTO dto = new EnrichedBookDTO(); // all fields null
        dto.setAuthors(List.of(new PersonRoleInputDTO(null, null, "Fallback Author", "author")));

        BookInputDTO result = transformer.transform(dto);

        assertNull(result.getTitle());
        assertEquals(List.of("Fallback Author"), result.getAuthors().stream().map(PersonRoleInputDTO::getLastName).toList());
        assertTrue(result.getEditors().isEmpty());
        assertNull(result.getLanguage());
        assertNull(result.getFormat());
        assertEquals("google books", result.getLocation());
        assertFalse(result.getInStock());
        assertEquals(List.of("Unsorted"), result.getCollections());
        assertNull(result.getOriginalLanguage());
        assertNull(result.getPublisher());
        assertNull(result.getPublicationYear());
        assertNull(result.getHistoricalDate());
        assertTrue(result.getOthers().isEmpty());
    }
}