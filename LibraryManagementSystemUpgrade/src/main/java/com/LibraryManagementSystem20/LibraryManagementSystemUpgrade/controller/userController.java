package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.controller;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BorrowedBookHistory;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.users;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service.bookService;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class userController {
    // user controller
    // here only to add and update

    @Autowired
    private userService userservice;

    @Autowired
    private bookService bookservice;

    @PostMapping("/create")
    public users createUser(@RequestBody users addUser){
        return userservice.createUser(addUser);
    }

    @PutMapping("/update/{id}")
    public users updateUser(@PathVariable String id, @RequestBody users update){
        return userservice.updateUsers(id, update);
    }

    @PostMapping("/{id}/borrow")
    public users borrowBook(@PathVariable String id, @RequestBody users bookDetails) {
        return userservice.borrowBookWithAutoDates(
                id,
                bookDetails.getTitle(),
                bookDetails.getAuthor(),
                bookDetails.getPublicationYear()
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userservice.deleteUser(id); // <-- use the instance, not class name
        return ResponseEntity.noContent().build();
    }



    // Return book and store in history
    @PostMapping("/{id}/return")
    public users returnBook(@PathVariable String id) {
        return userservice.returnBookAndStoreHistory(id);
    }

    // View borrowing history
    @GetMapping("/{id}/history")
    public List<BorrowedBookHistory> getHistory(@PathVariable String id) {
        return userservice.getBorrowingHistory(id);
    }


    // see avaliable books
    @GetMapping("/books")
    public List<books> allBooks(){
        return bookservice.getAllBooks();
    }

    // search for books based on title or author
    @GetMapping("/search/book/{word}")
    public List<books> searchBooks(@PathVariable String word){
        return bookservice.searchBooks(word);
    }

}
