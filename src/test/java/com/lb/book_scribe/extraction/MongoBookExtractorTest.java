package com.lb.book_scribe.extraction;

import com.lb.book_scribe.extraction.model.MongoBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MongoBookExtractorTest {

    private MongoBookRepo mockRepository;
    private MongoBookExtractor extractor;

    @BeforeEach
    void setUp() {
        mockRepository = mock(MongoBookRepo.class);
        extractor = new MongoBookExtractor(mockRepository);
    }

    @Test
    void searchByTitleAndAuthor_matchingBooks() {

        String title = "Test Book";
        String author = "John Doe";
        MongoBook book = new MongoBook(); // Set fields as needed
        when(mockRepository.findByVolumeInfoTitleAndVolumeInfoAuthorsContainingIgnoreCase(title, author))
                .thenReturn(List.of(book));

        List<MongoBook> result = extractor.searchByTitleAndAuthor(title, author);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(book, result.get(0));

    }



    @Test
    void searchByTitleAndAuthor_emptyList() {

        when(mockRepository.findByVolumeInfoTitleAndVolumeInfoAuthorsContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(List.of());

        List<MongoBook> result = extractor.searchByTitleAndAuthor("Nonexistent", "Nobody");

        assertNotNull(result);
        assertTrue(result.isEmpty());

    }



    @Test
    void testSearchByTitleAndAuthor_handlesNulls() {
        when(mockRepository.findByVolumeInfoTitleAndVolumeInfoAuthorsContainingIgnoreCase(null, null))
                .thenReturn(List.of());

        List<MongoBook> result = extractor.searchByTitleAndAuthor(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}