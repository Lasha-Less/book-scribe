package com.lb.book_scribe.extraction;

import com.lb.book_scribe.model.MongoBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoBookRepo extends MongoRepository<MongoBook, String> {

    List<MongoBook> findByVolumeInfoTitleContainingIgnoreCase(String title);

    List<MongoBook> findByVolumeInfoTitleAndVolumeInfoAuthorsContainingIgnoreCase(String title, String author);

    @Query("{'volumeInfo.publishedDate' : { $gte: ?0, $lte: ?1 } }")
    List<MongoBook> findByPublishedDateBetween(String fromYear, String toYear);

    // Custom query to filter by author and publishedDate range (string-based)
    @Query("{'volumeInfo.publishedDate' : { $gte: ?1, $lte: ?2 }, 'volumeInfo.authors' : { $regex: ?0, $options: 'i' } }")
    List<MongoBook> findByPublishedDateRangeAndAuthor(String author, String fromYear, String toYear);

}
