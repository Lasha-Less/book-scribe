package com.lb.book_scribe.extraction;

import com.lb.book_scribe.model.MongoBook;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoBookRepo extends MongoRepository<MongoBook, String> {

    List<MongoBook> findByTitleContainingIgnoreCase(String title);
    List<MongoBook> findByTitleAndVolumeInfoAuthorsContainingIgnoreCase(String title, String author);
    List<MongoBook> findByPublicationYearBetween(int startYear, int endYear);

    List<MongoBook> findByPublicationYearBetweenAndVolumeInfoAuthorsContainingIgnoreCase(
            int startYear, int endYear, String author);

    List<MongoBook> findByPublicationYearBetweenAndTitleContainingIgnoreCase(
            int startYear, int endYear, String title);

    List<MongoBook> findByPublicationYearBetweenAndVolumeInfoAuthorsContainingIgnoreCaseAndTitleContainingIgnoreCase(
            int startYear, int endYear, String author, String title);

}
