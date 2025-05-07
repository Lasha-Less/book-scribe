package com.lb.book_scribe.selection;

import com.lb.book_scribe.extraction.MongoBookExtractor;
import com.lb.book_scribe.model.MongoBook;
import com.lb.book_scribe.scraping.InfoLinkScraper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookSelectorRunner implements CommandLineRunner {

    private final MongoBookExtractor extractor;
    private final BookSelector selector;
    private final InfoLinkScraper scraper;

    public BookSelectorRunner(MongoBookExtractor extractor, BookSelector selector, InfoLinkScraper scraper) {
        this.extractor = extractor;
        this.selector = selector;
        this.scraper = new InfoLinkScraper();
    }

    @Override
    public void run(String... args) throws Exception {

        String title = "Logic of Sense";
        String author = "Deleuze";

        List<MongoBook> candidates = extractor.searchByTitleAndAuthor(title, author);

        for (MongoBook book : candidates) {
            if (book.getVolumeInfo() != null) {
                String infoLink = book.getVolumeInfo().getInfoLink();
                String canonical = book.getVolumeInfo().getCanonicalVolumeLink();
                scraper.scrape(infoLink, canonical); // simulate enrichment
            }
        }

        Optional<MongoBook> selected = selector.selectBestMatch(candidates);
        selected.ifPresentOrElse(
                book -> {
                    System.out.println("📌 Selected Book:");
                    System.out.println("Title: " + book.getVolumeInfo().getTitle());
                },
                () -> System.out.println("No book selected")
        );


    }
}
