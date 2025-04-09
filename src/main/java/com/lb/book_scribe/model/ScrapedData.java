package com.lb.book_scribe.model;

import lombok.Data;

import java.util.List;

@Data
public class ScrapedData {
    private List<String> editors;
    private List<String> translators;
    private List<String> categories;
    private String sourceUrl;
}
