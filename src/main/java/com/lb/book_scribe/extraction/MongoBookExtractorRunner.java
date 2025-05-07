package com.lb.book_scribe.extraction;

import com.lb.book_scribe.model.MongoBook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoBookExtractorRunner implements CommandLineRunner {
    /**
     * This is a test class remove when done testing extraction package classes and methods!!!
     */

    private final MongoBookExtractor extractor;

    public MongoBookExtractorRunner(MongoBookExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("Running MongoBookExtractorRunner...");
//
//
//        System.out.println();
//        System.out.println("Testing searchByTitle()");
//
//        List<MongoBook> books = extractor.searchByTitle("Logic of Sense");
//
//        System.out.println("Found " + books.size() + " book(s):");
//        books.forEach(book ->
//                System.out.println(book.getVolumeInfo().getTitle() + " | " + book.getVolumeInfo().getPublishedDate())
//        );

//        System.out.println();
//        System.out.println("Testing getBooksByPublishedDateRange()");

        // Set year range for testing (e.g., books between 1980 and 2020)
//        String fromYear = "1980";
//        String toYear = "2020";
//        String author = "Deleuze";
//        String title = "Logic of Sense";

//        List<MongoBook> booksWithPublishedDates = extractor.getBooksByPublishedDateRange(fromYear, toYear);

//        System.out.println("Books found between " + fromYear + " and " + toYear + ": " + booksWithPublishedDates.size());
//        booksWithPublishedDates.forEach(book -> {
//            System.out.println("TITLE: " + book.getVolumeInfo().getTitle()
//                    + " | Published Date: " + book.getVolumeInfo().getPublishedDate());
//        });

//        System.out.println();
//        System.out.println("Testing searchByPublishedDateRangeAndAuthor()");
//        List<MongoBook> booksWithAuthorsAndDates = extractor.searchByPublishedDateRangeAndAuthor(author, fromYear, toYear);
//
//        System.out.println("Books by " + author + " found between " + fromYear + " and " + toYear + ": " +
//                booksWithAuthorsAndDates.size());
//        booksWithAuthorsAndDates.forEach(book -> {
//            System.out.println("TITLE: " + book.getVolumeInfo().getTitle()
//                    + " | Published Date: " + book.getVolumeInfo().getPublishedDate());
//        });

//        System.out.println();
//        System.out.println("Testing searchByTitleAndAuthor()");
//
//        List<MongoBook> BooksWithTitleAndAuthor = extractor.searchByTitleAndAuthor(title, author);
//        System.out.println("Found " + BooksWithTitleAndAuthor.size() + " book(s):");
//        BooksWithTitleAndAuthor.forEach(book ->
//                System.out.println(book.getVolumeInfo().getTitle() + " | " + book.getVolumeInfo().getAuthors())
//        );

//        System.out.println();
//        System.out.println("Testing searchByTitleAndAuthorAndPublishedDateRange()");
//
//        List<MongoBook> booksWithAuthorTitleAndYears = extractor.searchByTitleAuthorAndPublishedDateRange(title, author, fromYear, toYear);
//
//        System.out.println("Books found for title '" + title + "', author '" + author + "' between " + fromYear + " and " + toYear + ": " + booksWithAuthorTitleAndYears.size());
//        booksWithAuthorTitleAndYears.forEach(book -> {
//            System.out.println("TITLE: " + book.getVolumeInfo().getTitle()
//                    + " | Published Date: " + book.getVolumeInfo().getPublishedDate());
//        });
    }

}
