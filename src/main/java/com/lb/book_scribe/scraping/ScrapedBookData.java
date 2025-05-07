package com.lb.book_scribe.scraping;

import lombok.Data;

import java.util.List;

@Data
public class ScrapedBookData {

    private List<String> editors;
    private List<String> translators;
    private String summary;
    private String sourceUrl;
    private List<String> isbns;

}
