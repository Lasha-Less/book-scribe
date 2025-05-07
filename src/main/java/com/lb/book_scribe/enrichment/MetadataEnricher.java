package com.lb.book_scribe.enrichment;

import com.lb.book_scribe.dto.*;
import com.lb.book_scribe.util.PersonNameParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataEnricher {

    private final GeminiEnrichmentService geminiService;

    public MetadataEnricher(GeminiEnrichmentService geminiService) {
        this.geminiService = geminiService;
    }

    public EnrichedBookDTO enrich(MongoBookDTO mongoBook,
                                  ScrapedDataDTO scrapedData,
                                  InterfaceInputDTO interfaceInput) {
        EnrichedBookDTO dto = new EnrichedBookDTO();

        // Step 1: Populate from MongoBookDTO
        applyMongoMetadata(dto, mongoBook);

        // Step 2: Apply ScrapedDataDTO
        applyScrapedMetadata(dto, scrapedData);

        // Step 3: Apply GPT enrichment
        geminiService.enrichMetadata(dto);

        // Step 4: Apply Interface Input (if present)
//        applyInterfaceInput(dto, interfaceInput);

        // Step 5: Apply Auto-fill defaults
        applyAutoFillDefaults(dto);

        return dto;
    }


    private void applyMongoMetadata(EnrichedBookDTO dto, MongoBookDTO mongo) {
        dto.setTitle(mongo.getTitle());
        dto.setLanguage(mongo.getLanguage());
        dto.setFormat(null); // no format in MongoBookDTO
        dto.setLocation(null); // no location in MongoBookDTO
        dto.setInStock(null); // no inStock in MongoBookDTO
        dto.setCollections(mongo.getCategories() != null ? new ArrayList<>(mongo.getCategories()) : new ArrayList<>());

        dto.setOriginalLanguage(null); // not in MongoBookDTO
        dto.setPublicationYear(extractYear(mongo.getPublishedDate()));
        dto.setHistoricalDate(null); // not present in MongoBookDTO
        dto.setPublisher(mongo.getPublisher());

        // All authors from Mongo are treated as authors for now
        dto.setAuthors(mapToPersonRole(mongo.getAuthors(), "Author"));

        // Initialize with mutable empty lists
        dto.setEditors(new ArrayList<>());
        dto.setOthers(new ArrayList<>());

        // Traceability
        dto.setMongoId(mongo.getId());
        dto.setInfoLink(mongo.getInfoLink());
        dto.setCanonicalVolumeLink(mongo.getCanonicalVolumeLink());
        dto.setSourceUrl(null); // only filled via scraping
    }


    private void applyScrapedMetadata(EnrichedBookDTO dto, ScrapedDataDTO scraped) {
        // Editors
        if (scraped.getEditors() != null) {
            List<PersonRoleInputDTO> parsedEditors = scraped.getEditors().stream()
                    .map(name -> PersonNameParser.parse(name, "Editor"))
                    .toList();
            dto.getEditors().addAll(parsedEditors);
        }

        // Translators
        if (scraped.getTranslators() != null) {
            List<PersonRoleInputDTO> parsedTranslators = scraped.getTranslators().stream()
                    .map(name -> PersonNameParser.parse(name, "Translator"))
                    .toList();
            dto.getOthers().addAll(parsedTranslators); // translators go to "others"
        }

        // ISBNs
        if (scraped.getIsbns() != null && !scraped.getIsbns().isEmpty()) {
            dto.setIsbns(new ArrayList<>(scraped.getIsbns()));
        }

        // Description
        if (scraped.getSummary() != null) {
            dto.setDescription(scraped.getSummary());
        }
    }


    private void applyInterfaceInput(EnrichedBookDTO dto, InterfaceInputDTO input) {
        // TODO: override or fill in fields from user input
    }

    private void applyAutoFillDefaults(EnrichedBookDTO dto) {
        if (dto.getFormat() == null) dto.setFormat("unknown");
        if (dto.getLocation() == null) dto.setLocation("google books");
        if (dto.getInStock() == null) dto.setInStock(false);
    }

    private List<PersonRoleInputDTO> mapToPersonRole(List<String> names, String role) {
        if (names == null) return new ArrayList<>();
        return names.stream()
                .map(name -> PersonNameParser.parse(name, role))
                .collect(java.util.stream.Collectors.toList());
    }

    private Integer extractYear(String publishedDate) {
        try {
            return (publishedDate != null && publishedDate.length() >= 4)
                    ? Integer.parseInt(publishedDate.substring(0, 4))
                    : null;
        } catch (Exception e) {
            return null;
        }
    }



}
