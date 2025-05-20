package com.lb.book_scribe.scraping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class InfoLinkScraperTest {

    private InfoLinkScraper scraper;

    @BeforeEach
    void setUp() {
        scraper = Mockito.spy(new InfoLinkScraper());
    }

    @Test
    void testScrape_returnsDataFromInfoLinkIfNotEmpty() {
        ScrapedBookData mockData = new ScrapedBookData();
        when(scraper.tryScrape("info-link")).thenReturn(mockData);
        doReturn(false).when(scraper).isEmpty(mockData);

        ScrapedBookData result = scraper.scrape("info-link", "canonical-link");

        assertSame(mockData, result);
        assertEquals("info-link", result.getSourceUrl());
    }

    @Test
    void testScrape_usesCanonicalWhenInfoLinkEmpty() {
        ScrapedBookData emptyData = new ScrapedBookData();
        ScrapedBookData canonicalData = new ScrapedBookData();

        when(scraper.tryScrape("info-link")).thenReturn(emptyData);
        when(scraper.tryScrape("canonical-link")).thenReturn(canonicalData);
        doReturn(true).when(scraper).isEmpty(emptyData);
        doReturn(false).when(scraper).isEmpty(canonicalData);

        ScrapedBookData result = scraper.scrape("info-link", "canonical-link");

        assertSame(canonicalData, result);
        assertEquals("canonical-link", result.getSourceUrl());
    }

    @Test
    void testScrape_returnsEmptyDataWhenBothFail() {
        when(scraper.tryScrape("info-link")).thenReturn(null);
        when(scraper.tryScrape("canonical-link")).thenReturn(null);
        doReturn(true).when(scraper).isEmpty(null);

        ScrapedBookData result = scraper.scrape("info-link", "canonical-link");

        assertNotNull(result);
        assertNull(result.getSourceUrl());
    }

}