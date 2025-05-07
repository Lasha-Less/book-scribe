package com.lb.book_scribe.scraping;

public class InfoLinkScraperRunner {

    public static void main(String[] args) {
        String infoLink = "https://books.google.nl/books?id=aQlbEAAAQBAJ&dq=intitle:Logic+of+Sense+inauthor:Gilles+Deleuze&hl=&source=gbs_api"; // Logic of Sense

        InfoLinkScraper scraper = new InfoLinkScraper();
        ScrapedBookData data = scraper.scrape(infoLink, null);

        System.out.println("Summary:\n" + data.getSummary());
        System.out.println("\nEditors: " + data.getEditors());
        System.out.println("Translators: " + data.getTranslators());
        System.out.println("ISBNs: " + data.getIsbns());
    }

    //TODO: 1. there are 11 rows in this table but when we run it only 10 is found.
    //TODO: 2. It skips the row where Subjects are listed, I think they might be buried deeper. Check it out.
    //TODO: 3. Make checking of labels compatible for variation between uppercase and lowercase.

}
