package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.service;

import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.BorrowedBookHistory;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.users;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields.books;
import com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.repo.userRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class userService {

    @Autowired
    private userRepo userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<users> searchUsers(String keyword) {
        return userRepository.findByUsernameIgnoreCaseOrUserEmailIgnoreCase(keyword, keyword);
    }

    public users createUser(users newUser) {
        int uniqueNumber = (int) (Math.random() * 100000);
        newUser.setId("user_" + uniqueNumber);
        newUser.setRole("USER");
        return userRepository.save(newUser);
    }

    public List<users> getAllUsers() {
        return userRepository.findAll();
    }

    public users updateUsers(String id, users userDetails){
        Optional<users> optionalUsers = userRepository.findById(id);
        if (optionalUsers.isPresent()){
            users user = optionalUsers.get();
            user.setUsername(userDetails.getUsername());
            user.setUserEmail(userDetails.getUserEmail());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    // delete user
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }



    /**
     * Existing method: borrow by userId + bookId (keeps the logic intact)
     */
    public users borrowBookWithAutoDates(String userId, String bookId) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        Query q = Query.query(Criteria.where("_id").is(bookId).and("copiesAvailable").gt(0));
        Update u = new Update().inc("copiesAvailable", -1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        books updatedBook = mongoTemplate.findAndModify(q, u, options, books.class);

        if (updatedBook == null) {
            throw new RuntimeException("Book not available or does not exist (id=" + bookId + ")");
        }

        LocalDate issue = LocalDate.now();
        LocalDate plannedReturn = issue.plusDays(5);

        user.setTitle(updatedBook.getTitle());
        user.setAuthor(updatedBook.getAuthor());
        user.setPublicationYear(updatedBook.getPublicationYear());
        user.setIssueDate(issue);
        user.setReturnDate(plannedReturn);

        if (user.getBorrowedBooksHistory() == null) {
            user.setBorrowedBooksHistory(new ArrayList<>());
        }

        return userRepository.save(user);
    }

    /**
     * NEW overloaded method: borrow by userId + (title, author, publicationYear)
     * This matches your controller which supplies those fields in the request body.
     */
    public users borrowBookWithAutoDates(String userId, String title, String author, int publicationYear) {
        // 1) find user
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        // 2) find a matching book document and atomically decrement copiesAvailable if > 0
        Query q = Query.query(
                Criteria.where("title").is(title)
                        .and("author").is(author)
                        .and("publicationYear").is(publicationYear)
                        .and("copiesAvailable").gt(0)
        );

        Update u = new Update().inc("copiesAvailable", -1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);

        books updatedBook = mongoTemplate.findAndModify(q, u, options, books.class);

        if (updatedBook == null) {
            throw new RuntimeException(String.format(
                    "Book not available (title='%s', author='%s', year=%d) or does not exist.",
                    title, author, publicationYear));
        }

        // 3) set user's current-borrowed fields and dates
        LocalDate issue = LocalDate.now();
        LocalDate plannedReturn = issue.plusDays(5); // or pass loanDays if you later want variable duration

        user.setTitle(updatedBook.getTitle());
        user.setAuthor(updatedBook.getAuthor());
        user.setPublicationYear(updatedBook.getPublicationYear());
        user.setIssueDate(issue);
        user.setReturnDate(plannedReturn);

        if (user.getBorrowedBooksHistory() == null) {
            user.setBorrowedBooksHistory(new ArrayList<>());
        }

        return userRepository.save(user);
    }

    /**
     * Return the current book: clear current fields, add history record, and increment book copiesAvailable
     */
    public users returnBookAndStoreHistory(String userId) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        if (user.getTitle() == null) {
            throw new RuntimeException("User has no borrowed book currently.");
        }

        // Create a history record
        BorrowedBookHistory history = new BorrowedBookHistory(
                user.getTitle(),
                user.getAuthor(),
                user.getIssueDate(),      // date collected
                LocalDate.now()           // actual return date
        );

        if (user.getBorrowedBooksHistory() == null) {
            user.setBorrowedBooksHistory(new ArrayList<>());
        }

        // add to history list
        user.getBorrowedBooksHistory().add(history);

        // increment the corresponding book's copiesAvailable by 1 (best-effort)
        try {
            Query q = Query.query(
                    Criteria.where("title").is(history.getTitle())
                            .and("author").is(history.getAuthor())
                            .and("publicationYear").is(history.getPublicationYear())
            );
            Update u = new Update().inc("copiesAvailable", 1);
            // use findAndModify so we get a document if present; if not found, we simply continue
            mongoTemplate.findAndModify(q, u, FindAndModifyOptions.options().returnNew(true), books.class);
        } catch (Exception ex) {
            // don't block return on this — log or handle accordingly in real app
            // e.g. logger.warn("Failed to increment book copiesAvailable", ex);
        }

        // clear current borrowed fields
        user.setTitle(null);
        user.setAuthor(null);
        user.setPublicationYear(0);
        user.setIssueDate(null);
        user.setReturnDate(null);

        return userRepository.save(user);
    }

    public List<BorrowedBookHistory> getBorrowingHistory(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId))
                .getBorrowedBooksHistory();
    }

    public users getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

}
