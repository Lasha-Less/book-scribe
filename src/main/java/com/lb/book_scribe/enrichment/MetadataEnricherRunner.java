package com.lb.book_scribe.enrichment;

import com.lb.book_scribe.dto.EnrichedBookDTO;
import com.lb.book_scribe.dto.InterfaceInputDTO;
import com.lb.book_scribe.dto.MongoBookDTO;
import com.lb.book_scribe.dto.ScrapedDataDTO;
import com.lb.book_scribe.extraction.MongoBookExtractor;
import com.lb.book_scribe.mapper.MongoBookMapper;
import com.lb.book_scribe.mapper.ScrapedDataMapper;
import com.lb.book_scribe.model.MongoBook;
import com.lb.book_scribe.scraping.InfoLinkScraper;
import com.lb.book_scribe.scraping.ScrapedBookData;
import com.lb.book_scribe.selection.BookSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MetadataEnricherRunner implements CommandLineRunner {

    private final MongoBookExtractor extractor;
    private final InfoLinkScraper scraper;
    private final MetadataEnricher enricher;
    private final MongoBookMapper mapper;
    private final ScrapedDataMapper scrapedMapper;
    private final BookSelector bookSelector;
    private final GeminiEnrichmentService geminiEnrichmentService;

    public MetadataEnricherRunner(
            MongoBookExtractor extractor,
            InfoLinkScraper scraper,
            MetadataEnricher enricher,
            MongoBookMapper mapper,
            ScrapedDataMapper scrapedMapper, BookSelector bookSelector, GeminiEnrichmentService geminiEnrichmentService) {
        this.extractor = extractor;
        this.scraper = scraper;
        this.enricher = enricher;
        this.mapper = mapper;
        this.scrapedMapper = scrapedMapper;
        this.bookSelector = bookSelector;
        this.geminiEnrichmentService = geminiEnrichmentService;
    }



    @Override
    public void run(String... args) {
        System.out.println("Running MetadataEnricherRunner...");
        System.out.println();

        // Use book with ID or title that matches Jean Khalfa as "author"
        List<MongoBook> candidates = extractor.searchByTitle("Introduction to the Philosophy of Gilles Deleuze");
        Optional<MongoBook> maybeSelected = bookSelector.selectBestMatch(candidates);

        if (maybeSelected.isEmpty()) {
            System.out.println("No suitable match found.");
            return;
        }

        MongoBook selected = maybeSelected.get();
        MongoBookDTO dto = mapper.toDto(selected);

        ScrapedBookData rawScraped = scraper.scrape(
                selected.getVolumeInfo().getInfoLink(),
                selected.getVolumeInfo().getCanonicalVolumeLink()
        );

        ScrapedDataDTO scraped = scrapedMapper.toDto(rawScraped);
        InterfaceInputDTO input = new InterfaceInputDTO();

        EnrichedBookDTO enriched = enricher.enrich(dto, scraped, input);

        System.out.println("✅ Enriched Result:");
        System.out.println(enriched);
        System.out.println();
        System.out.println("📘 Final Authors List:");
        enriched.getAuthors().forEach(a -> System.out.println(" - " + a.getFullName() + " (" + a.getRole() + ")"));

        System.out.println("📘 Final Editors List:");
        enriched.getEditors().forEach(e -> System.out.println(" - " + e.getFullName() + " (" + e.getRole() + ")"));

        System.out.println("📘 Final Contributors List:");
        enriched.getOthers().forEach(e -> System.out.println(" - " + e.getFullName() + " (" + e.getRole() + ")"));

        System.out.println();
        System.out.println("End of MetadataEnricherRunner...");
    }


}
