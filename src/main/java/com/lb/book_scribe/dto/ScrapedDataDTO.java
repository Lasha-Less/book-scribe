package com.lb.book_scribe.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScrapedDataDTO {
    private List<String> editors;
    private List<String> translators;
    private String sourceUrl;
    private String summary;
    private List<String> isbns;
}
