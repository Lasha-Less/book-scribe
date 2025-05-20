package com.lb.book_scribe.selection;

import com.lb.book_scribe.extraction.model.MongoBook;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookSelectorTest {

    private final BookSelector selector = new BookSelector();

    @Test
    void testSelectBestMatch_returnsBookWithHighestScore() {
        MongoBook lowScoreBook = createBook("A Book", List.of("Author A"), "1990");

        MongoBook highScoreBook = createBook("B Book", List.of("Author B"), "2022");
        highScoreBook.getVolumeInfo().setPublisher("Test Publisher");
        highScoreBook.getVolumeInfo().setDescription("A detailed book description");

        List<MongoBook> candidates = List.of(lowScoreBook, highScoreBook);
        Optional<MongoBook> result = selector.selectBestMatch(candidates);

        assertTrue(result.isPresent());
        assertEquals("B Book", result.get().getVolumeInfo().getTitle());
    }

    @Test
    void testSelectBestMatch_returnsEmptyWhenListIsEmpty() {
        Optional<MongoBook> result = selector.selectBestMatch(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectBestMatch_handlesNullFields() {
        MongoBook bookWithNulls = new MongoBook();
        MongoBook normalBook = createBook("Normal Book", List.of("Author X"), "2000");

        List<MongoBook> candidates = List.of(bookWithNulls, normalBook);
        Optional<MongoBook> result = selector.selectBestMatch(candidates);

        assertTrue(result.isPresent());
        assertEquals("Normal Book", result.get().getVolumeInfo().getTitle());
    }

    private MongoBook createBook(String title, List<String> authors, String year) {
        MongoBook.VolumeInfo info = new MongoBook.VolumeInfo();
        info.setTitle(title);
        info.setAuthors(authors);
        info.setPublishedDate(year);

        MongoBook book = new MongoBook();
        book.setVolumeInfo(info);
        return book;
    }
}