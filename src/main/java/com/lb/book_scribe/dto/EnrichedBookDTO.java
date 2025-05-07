package com.lb.book_scribe.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnrichedBookDTO {

    // BookInputDTO core fields
    private String title;
    private List<PersonRoleInputDTO> authors = new ArrayList<>();
    private List<PersonRoleInputDTO> editors = new ArrayList<>();
    private String language;
    private String format;
    private String location;
    private Boolean inStock;
    private List<String> collections = new ArrayList<>();

    // Optional fields from BookInputDTO
    private String originalLanguage;
    private Integer publicationYear;
    private Integer historicalDate;
    private String publisher;
    private List<PersonRoleInputDTO> others = new ArrayList<>();

    // book-scribe specific traceability fields (optional for preview/debug)
    private String mongoId;
    private String infoLink;
    private String canonicalVolumeLink;
    private String sourceUrl;
    private List<String> isbns;
    private String description;

    @Override
    public String toString() {
        return "EnrichedBookDTO {\n" +
                "  title='" + title + "',\n" +
                "  authors=" + authors + ",\n" +
                "  editors=" + editors + ",\n" +
                "  language='" + language + "',\n" +
                "  format='" + format + "',\n" +
                "  location='" + location + "',\n" +
                "  inStock=" + inStock + ",\n" +
                "  collections=" + collections + ",\n" +
                "  originalLanguage='" + originalLanguage + "',\n" +
                "  publicationYear=" + publicationYear + ",\n" +
                "  historicalDate=" + historicalDate + ",\n" +
                "  publisher='" + publisher + "',\n" +
                "  others=" + others + ",\n" +
                "  mongoId='" + mongoId + "',\n" +
                "  infoLink='" + infoLink + "',\n" +
                "  canonicalVolumeLink='" + canonicalVolumeLink + "',\n" +
                "  sourceUrl='" + sourceUrl + "'\n" +
                "  isbns='" + isbns + "'\n" +
                "  description='" + description + "'\n" +
                '}';
    }

}
