package com.lb.book_scribe.extraction;

import com.lb.book_scribe.model.MongoBook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoBookExtractor {

    private final MongoBookRepo repository;

    public MongoBookExtractor(MongoBookRepo repository) {
        this.repository = repository;
    }

    public List<MongoBook> searchByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public List<MongoBook> searchByTitleAndAuthor(String title, String author) {
        return repository.findByTitleAndVolumeInfoAuthorsContainingIgnoreCase(title, author);
    }

    public List<MongoBook> searchByYearRange(int from, int to) {
        return repository.findByPublicationYearBetween(from, to);
    }

    public List<MongoBook> searchByYearAndAuthor(int from, int to, String author) {
        return repository.findByPublicationYearBetweenAndVolumeInfoAuthorsContainingIgnoreCase(from, to, author);
    }

    public List<MongoBook> searchByYearAndTitle(int from, int to, String title) {
        return repository.findByPublicationYearBetweenAndTitleContainingIgnoreCase(from, to, title);
    }

    public List<MongoBook> searchByYearAuthorAndTitle(int from, int to, String author, String title) {
        return repository.findByPublicationYearBetweenAndVolumeInfoAuthorsContainingIgnoreCaseAndTitleContainingIgnoreCase(
                from, to, author, title
        );
    }


}
