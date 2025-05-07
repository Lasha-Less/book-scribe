package com.lb.book_scribe.extraction;

import com.lb.book_scribe.model.MongoBook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MongoBookExtractor {

    private final MongoBookRepo repository;

    public MongoBookExtractor(MongoBookRepo repository) {
        this.repository = repository;
    }

    public List<MongoBook> searchByTitle(String title) {
        return repository.findByVolumeInfoTitleContainingIgnoreCase(title);
    }

    public List<MongoBook> searchByTitleAndAuthor(String title, String author) {
        return repository.findByVolumeInfoTitleAndVolumeInfoAuthorsContainingIgnoreCase(title, author);
    }

    public Optional<Integer> extractYear(String publishedDate) {
        try {
            return Optional.of(Integer.parseInt(publishedDate.substring(0, 4)));
        } catch (Exception e) {
            return Optional.empty(); // malformed or missing date
        }
    }

    public List<MongoBook> getBooksByPublishedDateRange(String fromYear, String toYear) {
        return repository.findByPublishedDateBetween(fromYear, toYear);
    }



    // Method to extract books by author and published date range (using Strings for consistency)
    public List<MongoBook> searchByPublishedDateRangeAndAuthor(String author, String fromYear, String toYear) {
        // Call the custom query method in MongoBookRepo
        return repository.findByPublishedDateRangeAndAuthor(author, fromYear, toYear);
    }

    //YOU STILL NEED TO TEST THIS METHOD!
    // Method to search books by title, author, and published date range
    public List<MongoBook> searchByTitleAuthorAndPublishedDateRange(
            String title,
            String author,
            String fromYear,
            String toYear) {
        // Step 1: Search by title and author using repo method
        List<MongoBook> books = repository.findByVolumeInfoTitleAndVolumeInfoAuthorsContainingIgnoreCase(title, author);

        // Step 2: Filter books by published date range using the existing query in MongoBookRepo
        return books.stream()
                .filter(book -> {
                    Optional<Integer> year = extractYear(book.getVolumeInfo().getPublishedDate());
                    return year.isPresent() && year.get() >= Integer.parseInt(fromYear) && year.get() <=
                            Integer.parseInt(toYear);
                }).collect(Collectors.toList());
    }



}
