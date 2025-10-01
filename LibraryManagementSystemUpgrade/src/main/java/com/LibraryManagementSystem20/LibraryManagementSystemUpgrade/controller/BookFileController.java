package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.controller;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service.bookService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*") // dev only
public class BookFileController {

    private final bookService bookService;

    public BookFileController(bookService bookService) {
        this.bookService = bookService;
    }

    // GET /books/{id}/image  -> returns raw image bytes
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        books b = bookService.getBookById(id);
        if (b == null || b.getImage() == null || b.getImage().length == 0) {
            return ResponseEntity.notFound().build();
        }
        String mime = b.getImageMime() != null ? b.getImageMime() : "image/png";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sanitizeFilename(b.getTitle()) + "\"")
                .contentType(MediaType.parseMediaType(mime))
                .contentLength(b.getImage().length)
                .body(b.getImage());
    }

    // GET /books/{id}/pdf  -> returns raw pdf bytes
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable String id) {
        books b = bookService.getBookById(id);
        if (b == null || b.getBookFile() == null || b.getBookFile().length == 0) {
            return ResponseEntity.notFound().build();
        }
        String mime = b.getBookFileMime() != null ? b.getBookFileMime() : "application/pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sanitizeFilename(b.getTitle()) + ".pdf\"")
                .contentType(MediaType.parseMediaType(mime))
                .contentLength(b.getBookFile().length)
                .body(b.getBookFile());
    }

    private String sanitizeFilename(String s) {
        if (s == null) return "file";
        return s.replaceAll("[^a-zA-Z0-9\\.\\- _]", "_");
    }
}
