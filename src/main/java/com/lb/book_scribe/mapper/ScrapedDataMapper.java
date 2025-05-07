package com.lb.book_scribe.mapper;

import com.lb.book_scribe.dto.ScrapedDataDTO;
import com.lb.book_scribe.scraping.ScrapedBookData;
import org.springframework.stereotype.Component;

@Component
public class ScrapedDataMapper {

    public ScrapedDataDTO toDto(ScrapedBookData data) {
        ScrapedDataDTO dto = new ScrapedDataDTO();
        dto.setEditors(data.getEditors());
        dto.setTranslators(data.getTranslators());
        dto.setSourceUrl(data.getSourceUrl());
        dto.setSummary(data.getSummary());
        dto.setIsbns(data.getIsbns());
        return dto;
    }
}
