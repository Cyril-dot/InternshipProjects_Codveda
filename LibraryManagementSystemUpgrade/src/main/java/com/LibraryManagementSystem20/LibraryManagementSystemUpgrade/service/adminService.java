package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.admin;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.users;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.repo.adminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class adminService {
    @Autowired
    private bookService bookService;
    @Autowired
    private userService userService;

    @Autowired
    private adminRepo adminrepo;

    // to get all users
    public List<users> getAllUsers() {
        return userService.getAllUsers();
    }

    // to get all books (DTOs are available on bookService)
    public List<books> getAllBooks() {
        return bookService.getAllBooks();
    }

    // to add book
    public books addBookWithFiles(books book, MultipartFile image, MultipartFile pdf) throws IOException {
        return bookService.addBookWithFiles(book, image, pdf);
    }

    // keep simple wrapper for addBook if you still need it:
    public books addBook(books newBook) {
        return bookService.addBook(newBook);
    }


    // update book details
    public books updateBook(String id, books bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    // delete book
    public void deleteBook(String id) {
        bookService.deleteBook(id);
    }

    // find book by id
    public List<books> searchBookById(String id){
        return Collections.singletonList(bookService.getBookById(id));
    }

    // find user by id
    public List<users> searchUserId(String id){
        return Collections.singletonList(userService.getUserById(id));
    }

    //update the number of copies available
    public books updateCopiesAvailable(String id, int newCopies) {
        return bookService.updateCopiesAvailable(id, newCopies);
    }

    // to search for a particular book
    public List<books> searchBooks(String keyword){
        return bookService.searchBooks(keyword);
    }

    public admin addAdmin(admin add) {
        int uniqueNumber = (int) (Math.random() * 1000);
        add.setAdminId("admin_" + uniqueNumber);
        add.setRole("ADMIN");
        return adminrepo.save(add);
    }
}
