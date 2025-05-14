package com.lb.book_scribe.transformation;

import com.lb.book_scribe.dto.BookInputDTO;
import com.lb.book_scribe.dto.EnrichedBookDTO;
import com.lb.book_scribe.dto.PersonRoleInputDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MetadataTransformer {

    public BookInputDTO transform(EnrichedBookDTO dto) throws IllegalAccessException {
        BookInputDTO result = new BookInputDTO(
                normalize(dto.getTitle()),
                dto.getAuthors(),
                dto.getEditors(),
                normalize(dto.getLanguage()),
                normalize(dto.getFormat()),
                normalizeOrDefault(dto.getLocation(), "google books"),
                dto.getInStock() != null ? dto.getInStock() : false,
                dto.getCollections() != null ? dto.getCollections() : List.of("Unsorted")
        );

        result.setOriginalLanguage(normalize(dto.getOriginalLanguage()));
        result.setPublisher(normalize(dto.getPublisher()));
        result.setPublicationYear(dto.getPublicationYear());
        result.setHistoricalDate(dto.getHistoricalDate());
        result.setOthers(dto.getOthers());

        return result;
    }

    private String normalize(String s) {
        return s != null ? s.trim() : null;
    }

    private String normalizeOrDefault(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s.trim();
    }

    private List<PersonRoleInputDTO> toPersonRoleList(List<String> names, String role) {
        if (names == null) return new ArrayList<>();
        return names.stream()
                .map(name -> new PersonRoleInputDTO(null, null, name.trim(), role))
                .toList();
    }

}
