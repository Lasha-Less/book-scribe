package com.lb.book_scribe.controller;

import com.lb.book_scribe.dto.BookInputDTO;
import com.lb.book_scribe.service.BookImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transform")
public class BookTransformController {

    private final BookImportService bookImportService;

    public BookTransformController(BookImportService bookImportService) {
        this.bookImportService = bookImportService;
    }

    @GetMapping
    public ResponseEntity<BookInputDTO> transformBook(
            @RequestParam String title,
            @RequestParam String author) throws IllegalAccessException {

        BookInputDTO result = bookImportService.transformBook(title, author);
        return ResponseEntity.ok(result);
    }

}
