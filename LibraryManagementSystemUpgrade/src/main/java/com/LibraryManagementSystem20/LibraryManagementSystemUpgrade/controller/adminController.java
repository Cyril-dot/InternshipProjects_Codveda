package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.controller;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BookDto;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.admin;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.users;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service.adminService;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service.bookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class adminController {
    // controller for admin

    private static final Logger log = LoggerFactory.getLogger(adminController.class);

    @Autowired
    private adminService adminServices;

    @Autowired
    private bookService service;

    @GetMapping("/users")
    public List<users> users() {
        return adminServices.getAllUsers();
    }

    // get user by id
    @GetMapping("/user/{id}")
    public List<users> searchUserById(@PathVariable String id){
        return adminServices.searchUserId(id);
    }

    @GetMapping("/books")
    public List<BookDto> listBookDtos() {
        return service.getAllBookDtos();  // returns base64 fields or no binaries
    }

    @GetMapping("/searchBook")
    public List<books> searchUsers(@RequestParam String keyword){
        return adminServices.searchBooks(keyword);
    }

    @GetMapping("/book/{id}")
    public List<books> searchById(@PathVariable String id){
        return adminServices.searchBookById(id);
    }

    @PostMapping(value = "/upload/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(name = "publicationYear", required = false) Integer publicationYear,
            @RequestParam(name = "copiesAvailable", required = false) Integer copiesAvailable,
            @RequestParam(name = "image", required = false) MultipartFile imageFile,
            @RequestParam(name = "pdf", required = false) MultipartFile pdfFile
    ) {
        try {
            // Basic validation
            if (title == null || title.isBlank() || author == null || author.isBlank()) {
                return ResponseEntity.badRequest().body("title and author are required");
            }

            books book = new books();
            book.setTitle(title);
            book.setAuthor(author);
            if (publicationYear != null) book.setPublicationYear(publicationYear);
            book.setCopiesAvailable(copiesAvailable == null ? 1 : copiesAvailable);

            // log upload sizes (if present)
            if (imageFile != null) {
                log.info("Received image file: name={}, size={}", imageFile.getOriginalFilename(), imageFile.getSize());
            }
            if (pdfFile != null) {
                log.info("Received pdf file: name={}, size={}", pdfFile.getOriginalFilename(), pdfFile.getSize());
            }

            // delegate to service which should handle null files safely
            books saved = adminServices.addBookWithFiles(book, imageFile, pdfFile);

            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            // Log full stack trace so you can see why the socket was reset
            log.error("Error handling /upload/book", ex);
            // return an error response — avoids abrupt connection resets
            return ResponseEntity.status(500).body("Server error: " + ex.getMessage());
        }
    }

    // upload admin
    @PostMapping("/upload/admin")
    public admin addAdmin(@RequestBody admin add) {
        return adminServices.addAdmin(add);
    }

    // update book
    @PutMapping("/update/book/{id}")
    public books updateBook(@PathVariable String id, @RequestBody books book){
        return adminServices.updateBook(id, book);
    }

    // to delete books
    @DeleteMapping("/delete/book/{id}")
    public String deleteBook(@PathVariable String id){
        adminServices.deleteBook(id);
        return "User deleted successfully!";
    }

    //update copies avaliable
    @PatchMapping("/singleUpdate/book/{id}")
    public books updateCopiesAvailable(@PathVariable String id, @RequestBody int copiesAvailable) {
        return adminServices.updateCopiesAvailable(id, copiesAvailable);
    }
}
