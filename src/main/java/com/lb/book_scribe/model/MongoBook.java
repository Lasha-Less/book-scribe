package com.lb.book_scribe.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Document("book_extracted_data")
public class MongoBook {

    @Id
    private String id;

    private VolumeInfo volumeInfo;
    private AccessInfo accessInfo;

    @Data
    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;
        private String description;
        private List<String> categories;
        private String language;
        private String infoLink;
        private String canonicalVolumeLink;
        private ImageLinks imageLinks;
    }

    @Data
    public static class ImageLinks {
        private String smallThumbnail;
        private String thumbnail;
    }

    @Data
    public static class AccessInfo {
        private FormatAvailability epub;
        private FormatAvailability pdf;
    }

    @Data
    public static class FormatAvailability {
        private boolean isAvailable;
    }

}
