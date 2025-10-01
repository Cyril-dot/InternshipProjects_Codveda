package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.repo;


import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;


public interface bookRepo extends MongoRepository<books, String> {
    List<books> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    List<books> findByTitleIgnoreCaseOrAuthorIgnoreCaseOrPublicationYear(
            String title, String author, int publicationYear);
}