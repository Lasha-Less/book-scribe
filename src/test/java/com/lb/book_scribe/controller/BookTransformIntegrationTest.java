package com.lb.book_scribe.controller;

import com.lb.book_scribe.extraction.model.MongoBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BookTransformIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setupTestData() {
        MongoBook.VolumeInfo info = new MongoBook.VolumeInfo();
        info.setTitle("Test Book");
        info.setAuthors(List.of("Test Author"));
        info.setInfoLink("https://example.com/info");
        info.setCanonicalVolumeLink("https://example.com/canonical");

        MongoBook book = new MongoBook();
        book.setVolumeInfo(info);

        mongoTemplate.save(book);
    }

    @Test
    void testTransform_returnsEnrichedTransformedBook() throws Exception {
        mockMvc.perform(get("/transform")
                        .param("title", "Test Book")
                        .param("author", "Test Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.authors[0].lastName").value("Author"))
                .andExpect(jsonPath("$.location").value("google books"));
    }

    @Test
    void testTransform_fallbackToTitleOnlyWhenAuthorFails() throws Exception {
        // Clear DB
        mongoTemplate.dropCollection(MongoBook.class);

        // Insert book with only matching title (not author)
        MongoBook.VolumeInfo info = new MongoBook.VolumeInfo();
        info.setTitle("Fallback Book");
        info.setAuthors(List.of("Another Author")); // Not "Test Author"
        info.setInfoLink("https://example.com/info");
        info.setCanonicalVolumeLink("https://example.com/canonical");

        MongoBook fallbackBook = new MongoBook();
        fallbackBook.setVolumeInfo(info);
        mongoTemplate.save(fallbackBook);

        // Trigger search with author that won't match
        mockMvc.perform(get("/transform")
                        .param("title", "Fallback Book")
                        .param("author", "Test Author")) // mismatch
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fallback Book"))
                .andExpect(jsonPath("$.authors").isArray());// fallback author's lastName
    }

    @Test
    void testTransform_returns404WhenNoMatchFound() throws Exception {
        // Clear DB to ensure no matches
        mongoTemplate.dropCollection(MongoBook.class);

        mockMvc.perform(get("/transform")
                        .param("title", "Nonexistent Book")
                        .param("author", "Ghost Author"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("No suitable book found")));
    }


}
