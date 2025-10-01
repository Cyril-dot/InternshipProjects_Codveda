package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBookHistory {
    private String bookId;           // NEW - unique MongoDB _id of the book
    private String title;
    private String author;
    private int publicationYear;     // also optional but nice to show
    private LocalDate issueDate;     // date collected
    private LocalDate returnedDate;  // actual return date

    public BorrowedBookHistory(String title, String author, LocalDate issueDate, LocalDate now) {
    }
}
