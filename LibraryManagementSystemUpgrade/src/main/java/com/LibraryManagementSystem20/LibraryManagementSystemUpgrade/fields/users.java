package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;                     // ✅ use Spring Data Mongo
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class users {                                           // ✅ class name capitalized

    @Id
    private String id;

    private String username;
    private String userEmail;

    // current borrowed book info
    private String title;
    private String author;
    private int publicationYear;
    private LocalDate issueDate;
    private LocalDate returnDate;

    private String role; // e.g., "USER"

    // history of borrowed books
    private List<BorrowedBookHistory> borrowedBooksHistory = new ArrayList<>();

    public void setRole(String role) {
        this.role = "USER";
    }
}
