package com.lb.book_scribe.service;

import com.lb.book_scribe.dto.*;
import com.lb.book_scribe.enrichment.MetadataEnricher;
import com.lb.book_scribe.exception.BookNotFoundException;
import com.lb.book_scribe.extraction.MongoBookExtractor;
import com.lb.book_scribe.extraction.model.MongoBook;
import com.lb.book_scribe.scraping.InfoLinkScraper;
import com.lb.book_scribe.scraping.ScrapedBookData;
import com.lb.book_scribe.selection.BookSelector;
import com.lb.book_scribe.transformation.MetadataTransformer;
import com.lb.book_scribe.transformation.mapper.MongoBookMapper;
import com.lb.book_scribe.transformation.mapper.ScrapedDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookImportServiceTest {

    private MongoBookExtractor extractor;
    private InfoLinkScraper scraper;
    private MetadataEnricher enricher;
    private MetadataTransformer transformer;
    private BookSelector selector;
    private MongoBookMapper mongoMapper;
    private ScrapedDataMapper scrapedMapper;
    private BookImportService service;

    @BeforeEach
    void setUp() {
        extractor = mock(MongoBookExtractor.class);
        scraper = mock(InfoLinkScraper.class);
        enricher = mock(MetadataEnricher.class);
        transformer = mock(MetadataTransformer.class);
        selector = mock(BookSelector.class);
        mongoMapper = mock(MongoBookMapper.class);
        scrapedMapper = mock(ScrapedDataMapper.class);

        service = new BookImportService(extractor, scraper, enricher, transformer, selector, mongoMapper, scrapedMapper);
    }

    @Test
    void testTransformBook_successfulPath() throws IllegalAccessException {
        // Setup
        String title = "Test Title";
        String author = "Test Author";

        MongoBook book = new MongoBook();
        MongoBook.VolumeInfo info = new MongoBook.VolumeInfo();
        info.setInfoLink("infoLink");
        info.setCanonicalVolumeLink("canonicalLink");
        book.setVolumeInfo(info);

        ScrapedBookData scrapedData = new ScrapedBookData();
        MongoBookDTO mongoDto = new MongoBookDTO();
        ScrapedDataDTO scrapedDto = new ScrapedDataDTO();
        EnrichedBookDTO enrichedDto = new EnrichedBookDTO();
        BookInputDTO expected = new BookInputDTO();

        when(extractor.searchByTitleAndAuthor(title, author)).thenReturn(List.of(book));
        when(selector.selectBestMatch(List.of(book))).thenReturn(Optional.of(book));
        when(scraper.scrape("infoLink", "canonicalLink")).thenReturn(scrapedData);
        when(mongoMapper.toDto(book)).thenReturn(mongoDto);
        when(scrapedMapper.toDto(scrapedData)).thenReturn(scrapedDto);
        when(enricher.enrich(mongoDto, scrapedDto, new InterfaceInputDTO())).thenReturn(enrichedDto);
        when(transformer.transform(enrichedDto)).thenReturn(expected);

        // Execute
        BookInputDTO result = service.transformBook(title, author);

        // Verify
        assertSame(expected, result);
        verify(extractor).searchByTitleAndAuthor(title, author);
        verify(selector).selectBestMatch(List.of(book));
        verify(scraper).scrape("infoLink", "canonicalLink");
        verify(mongoMapper).toDto(book);
        verify(scrapedMapper).toDto(scrapedData);
        verify(enricher).enrich(mongoDto, scrapedDto, new InterfaceInputDTO());
        verify(transformer).transform(enrichedDto);
    }

    @Test
    void testTransformBook_fallbackToTitleSearch() throws IllegalAccessException {
        String title = "Fallback Title";
        String author = "Fallback Author";

        MongoBook fallbackBook = new MongoBook();
        MongoBook.VolumeInfo info = new MongoBook.VolumeInfo();
        info.setInfoLink("infoLink");
        info.setCanonicalVolumeLink("canonicalLink");
        fallbackBook.setVolumeInfo(info);

        when(extractor.searchByTitleAndAuthor(title, author)).thenReturn(List.of());
        when(selector.selectBestMatch(List.of())).thenReturn(Optional.empty());
        when(extractor.searchByTitle(title)).thenReturn(List.of(fallbackBook));
        when(selector.selectBestMatch(List.of(fallbackBook))).thenReturn(Optional.of(fallbackBook));

        when(scraper.scrape(any(), any())).thenReturn(new ScrapedBookData());
        when(mongoMapper.toDto(any())).thenReturn(new MongoBookDTO());
        when(scrapedMapper.toDto(any())).thenReturn(new ScrapedDataDTO());
        when(enricher.enrich(any(), any(), any())).thenReturn(new EnrichedBookDTO());
        when(transformer.transform(any())).thenReturn(new BookInputDTO());

        BookInputDTO result = service.transformBook(title, author);
        assertNotNull(result);
    }

    @Test
    void testTransformBook_throwsWhenNoMatchFound() {
        when(extractor.searchByTitleAndAuthor("X", "Y")).thenReturn(List.of());
        when(selector.selectBestMatch(List.of())).thenReturn(Optional.empty());
        when(extractor.searchByTitle("X")).thenReturn(List.of());
        when(selector.selectBestMatch(List.of())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> service.transformBook("X", "Y"));
    }
}