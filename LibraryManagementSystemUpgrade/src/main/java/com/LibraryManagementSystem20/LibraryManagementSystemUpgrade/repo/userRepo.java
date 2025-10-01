package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.repo;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface userRepo extends MongoRepository<users, String> {
    List<users> findByUsernameIgnoreCaseOrUserEmailIgnoreCase(String username, String userEmail);
}
