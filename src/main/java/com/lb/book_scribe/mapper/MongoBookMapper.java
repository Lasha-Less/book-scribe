package com.lb.book_scribe.mapper;

import com.lb.book_scribe.dto.MongoBookDTO;
import com.lb.book_scribe.model.MongoBook;
import org.springframework.stereotype.Component;

@Component
public class MongoBookMapper {

    public MongoBookDTO toDto(MongoBook book) {
        MongoBookDTO dto = new MongoBookDTO();

        dto.setId(book.getId());

        if (book.getVolumeInfo() != null) {
            MongoBook.VolumeInfo v = book.getVolumeInfo();
            dto.setTitle(v.getTitle());
            dto.setAuthors(v.getAuthors());
            dto.setPublisher(v.getPublisher());
            dto.setPublishedDate(v.getPublishedDate());
            dto.setDescription(v.getDescription());
            dto.setCategories(v.getCategories());
            dto.setLanguage(v.getLanguage());
            dto.setInfoLink(v.getInfoLink());
            dto.setCanonicalVolumeLink(v.getCanonicalVolumeLink());

            if (v.getImageLinks() != null) {
                dto.setThumbnail(v.getImageLinks().getThumbnail());
                dto.setSmallThumbnail(v.getImageLinks().getSmallThumbnail());
            }

            if (book.getAccessInfo() != null) {
                dto.setPdfAvailable(book.getAccessInfo().isPdfAvailable());
                dto.setEpubAvailable(book.getAccessInfo().isEpubAvailable());
            }
        }

        return dto;
    }
}
