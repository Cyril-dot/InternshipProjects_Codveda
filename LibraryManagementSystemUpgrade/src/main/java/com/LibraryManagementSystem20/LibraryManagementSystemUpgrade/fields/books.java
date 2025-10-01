package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class books {
    @Id
    private String id;


    private String title;
    private String author;


    // binary payloads stored as byte[] (Spring Data Mongo will map this to Binary)
    private byte[] image;
    private String imageMime;


    private byte[] bookFile;
    private String bookFileMime;


    private int publicationYear;
    private int copiesAvailable;


    private Instant createdAt = Instant.now();


    // convenience helpers to set bytes from MultipartFile (used by service/controller)
    public void setImageFromMultipart(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            this.image = file.getBytes();
            this.imageMime = file.getContentType();
        }
    }


    public void setPdfFromMultipart(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            this.bookFile = file.getBytes();
            this.bookFileMime = file.getContentType();
        }
    }
}