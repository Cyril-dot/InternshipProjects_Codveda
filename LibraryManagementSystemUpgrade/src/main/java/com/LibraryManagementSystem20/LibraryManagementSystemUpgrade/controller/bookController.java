package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.controller;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service.bookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/shelf")
public class bookController {

    @Autowired
    private bookService bookService;

    // create book with optional image/pdf upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam(required = false) Integer copiesAvailable,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false, name = "pdf") MultipartFile pdf
    ) throws IOException {
        books book = new books();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublicationYear(publicationYear != null ? publicationYear : 0);
        book.setCopiesAvailable(copiesAvailable != null ? copiesAvailable : 1);

        String id = String.valueOf(bookService.addBookWithFiles(book, image, pdf));
        return ResponseEntity.ok(id);
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> listBooks() {
        return ResponseEntity.ok(bookService.getAllBookDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto> getById(@PathVariable String id) {
        com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto dto = bookService.getBookDtoById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        books b = bookService.getBookById(id);
        if (b == null || b.getImage() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(b.getImageMime() != null ? b.getImageMime() : MediaType.IMAGE_PNG_VALUE))
                .body(b.getImage());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable String id) {
        books b = bookService.getBookById(id);
        if (b == null || b.getBookFile() == null) return ResponseEntity.notFound().build();
        String mime = b.getBookFileMime() != null ? b.getBookFileMime() : MediaType.APPLICATION_PDF_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mime))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + (b.getTitle() != null ? b.getTitle().replaceAll("\"","\\\"") : "file") + ".pdf\"")
                .body(b.getBookFile());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}