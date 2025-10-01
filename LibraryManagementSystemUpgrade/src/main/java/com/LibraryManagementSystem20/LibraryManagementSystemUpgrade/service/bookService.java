package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service;


import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.repo.bookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class bookService {

    @Autowired
    private bookRepo bookRepo;

    // Return raw entity list
    public List<books> getAllBooks() {
        return bookRepo.findAll();
    }

    // Return DTOs with base64 payloads (convenient for front-end)
    public List<com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto> getAllBookDtos() {
        return getAllBooks().stream().map(this::toDto).collect(Collectors.toList());
    }

    public books getBookById(String id) {
        return bookRepo.findById(id).orElse(null);
    }

    public com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto getBookDtoById(String id) {
        books b = getBookById(id);
        return b == null ? null : toDto(b);
    }

    // Add book where image/pdf come as MultipartFile (typical REST upload)

    public books addBookWithFiles(books book, MultipartFile image, MultipartFile pdf) throws IOException {
        if (image != null && !image.isEmpty()) {
            book.setImage(image.getBytes());
            book.setImageMime(image.getContentType());
        }
        if (pdf != null && !pdf.isEmpty()) {
            book.setBookFile(pdf.getBytes());
            book.setBookFileMime(pdf.getContentType());
        }
        int uniqueNumber = (int) (Math.random() * 10000000);
        book.setId("book_" + uniqueNumber);
        return bookRepo.save(book);
    }

    // for callers that already have bytes set on book
    public books addBook(books book) {
        if (book.getId() == null) {
            int uniqueNumber = (int) (Math.random() * 10000000);
            book.setId("book_" + uniqueNumber);
        }
        return bookRepo.save(book);
    }

    public books updateBook(String id, books bookDetails) {
        books existing = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found: " + id));
        existing.setTitle(bookDetails.getTitle());
        existing.setAuthor(bookDetails.getAuthor());
        existing.setPublicationYear(bookDetails.getPublicationYear());
        existing.setCopiesAvailable(bookDetails.getCopiesAvailable());

        if (bookDetails.getImage() != null && bookDetails.getImage().length > 0) {
            existing.setImage(bookDetails.getImage());
            existing.setImageMime(bookDetails.getImageMime());
        }
        if (bookDetails.getBookFile() != null && bookDetails.getBookFile().length > 0) {
            existing.setBookFile(bookDetails.getBookFile());
            existing.setBookFileMime(bookDetails.getBookFileMime());
        }

        return bookRepo.save(existing);
    }

    public void deleteBook(String id) {
        bookRepo.deleteById(id);
    }

    public books updateCopiesAvailable(String id, int newCopies) {
        books b = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found: " + id));
        b.setCopiesAvailable(newCopies);
        return bookRepo.save(b);
    }

    public List<books> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllBooks();
        return bookRepo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    // Helper to convert entity -> DTO (Base64 encode binary data)
    private com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto toDto(books b) {
        com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto dto = new com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto();
        dto.setId(b.getId());
        dto.setTitle(b.getTitle());
        dto.setAuthor(b.getAuthor());

        if (b.getImage() != null && b.getImage().length > 0) {
            dto.setImageBase64(Base64.getEncoder().encodeToString(b.getImage()));
            dto.setImageMime(b.getImageMime());
        }
        if (b.getBookFile() != null && b.getBookFile().length > 0) {
            dto.setPdfBase64(Base64.getEncoder().encodeToString(b.getBookFile()));
            dto.setPdfMime(b.getBookFileMime());
        }

        dto.setPublicationYear(b.getPublicationYear());
        dto.setCopiesAvailable(b.getCopiesAvailable());
        return dto;
    }
}

