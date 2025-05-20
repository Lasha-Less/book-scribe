package com.lb.book_scribe.service;

import com.lb.book_scribe.dto.*;
import com.lb.book_scribe.enrichment.MetadataEnricher;
import com.lb.book_scribe.exception.BookNotFoundException;
import com.lb.book_scribe.extraction.MongoBookExtractor;
import com.lb.book_scribe.transformation.mapper.MongoBookMapper;
import com.lb.book_scribe.transformation.mapper.ScrapedDataMapper;
import com.lb.book_scribe.extraction.model.MongoBook;
import com.lb.book_scribe.scraping.InfoLinkScraper;
import com.lb.book_scribe.scraping.ScrapedBookData;
import com.lb.book_scribe.selection.BookSelector;
import com.lb.book_scribe.transformation.MetadataTransformer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookImportService {

    private final MongoBookExtractor extractor;
    private final InfoLinkScraper scraper;
    private final MetadataEnricher enricher;
    private final MetadataTransformer transformer;
    private final BookSelector selector;
    private final MongoBookMapper mongoBookMapper;
    private final ScrapedDataMapper scrapedDataMapper;


    public BookImportService(MongoBookExtractor extractor,
                             InfoLinkScraper scraper,
                             MetadataEnricher enricher,
                             MetadataTransformer transformer,
                             BookSelector selector,
                             MongoBookMapper mapper, ScrapedDataMapper scrapedDataMapper) {
        this.extractor = extractor;
        this.scraper = scraper;
        this.enricher = enricher;
        this.transformer = transformer;
        this.selector = selector;
        this.mongoBookMapper = mapper;
        this.scrapedDataMapper = scrapedDataMapper;
    }

    public BookInputDTO transformBook(String title, String author) throws IllegalAccessException {
        // Step 1: Try search by title + author
        List<MongoBook> candidates = extractor.searchByTitleAndAuthor(title, author);
        MongoBook selected = selector.selectBestMatch(candidates)
                .orElseGet(() -> {
                    // Step 2: Fallback to title-only search if no match
                    List<MongoBook> fallbackCandidates = extractor.searchByTitle(title);
                    return selector.selectBestMatch(fallbackCandidates)
                            .orElseThrow(() -> new BookNotFoundException("No suitable book found for: " + title));
                });

        // Step 3: Scrape
        var volumeInfo = selected.getVolumeInfo();
        ScrapedBookData rawScraped = scraper.scrape(volumeInfo.getInfoLink(), volumeInfo.getCanonicalVolumeLink());

        // Step 4: Map to DTOs
        MongoBookDTO mongoDTO = mongoBookMapper.toDto(selected);
        ScrapedDataDTO scrapedDTO = scrapedDataMapper.toDto(rawScraped);

        // Step 5: Enrich
        EnrichedBookDTO enrichedDTO = enricher.enrich(mongoDTO, scrapedDTO, new InterfaceInputDTO());

        // Step 6: Transform
        return transformer.transform(enrichedDTO);
    }

}
