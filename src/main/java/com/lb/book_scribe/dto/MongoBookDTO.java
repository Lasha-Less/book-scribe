package com.lb.book_scribe.dto;

import lombok.Data;

import java.util.List;

@Data
public class MongoBookDTO {
    private String id; // MongoDB ObjectId
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private List<String> categories;
    private String language;
    private String infoLink;
    private String canonicalVolumeLink;
    private String thumbnail; // imageLinks.thumbnail
    private String smallThumbnail; // imageLinks.smallThumbnail
    private boolean pdfAvailable;
    private boolean epubAvailable;
}
