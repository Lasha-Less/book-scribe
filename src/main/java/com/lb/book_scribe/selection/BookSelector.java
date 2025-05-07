package com.lb.book_scribe.selection;

import com.lb.book_scribe.model.MongoBook;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class BookSelector {

    public Optional<MongoBook> selectBestMatch(List<MongoBook> candidates) {
        System.out.println("Found " + candidates.size() + " candidates.");

        return candidates.stream()
                .sorted(Comparator.comparingInt(this::score).reversed())
                .peek(book -> {
                    int score = score(book);
                    String title = safe(book.getVolumeInfo() != null ? book.getVolumeInfo().getTitle() : null);
                    String authors = safe(book.getVolumeInfo() != null ?
                            String.join(", ", book.getVolumeInfo().getAuthors()) : null);
                    String year = safe(book.getVolumeInfo() != null ? book.getVolumeInfo().getPublishedDate() : null);
                    System.out.println("Score: " + score + " → " + title + " | " + authors + " | " + year);
                })
                .limit(5)
                .findFirst();
    }

    private String safe(String input) {
        return input != null ? input : "N/A";
    }




    private int score(MongoBook book) {
        int score = 0;

        // Example heuristics (to be expanded)
        if (book.getVolumeInfo() != null && book.getVolumeInfo().getTitle() != null &&
                !book.getVolumeInfo().getTitle().isBlank()) score += 2;
        if (book.getVolumeInfo() != null) {
            if (book.getVolumeInfo().getAuthors() != null && !book.getVolumeInfo().getAuthors().isEmpty()) score += 2;
            if (book.getVolumeInfo().getPublisher() != null) score += 1;
            if (book.getVolumeInfo().getDescription() != null) score += 1;
        }
        if (book.getVolumeInfo() != null && book.getVolumeInfo().getPublishedDate() != null) score +=1;

        return score;
    }

}
