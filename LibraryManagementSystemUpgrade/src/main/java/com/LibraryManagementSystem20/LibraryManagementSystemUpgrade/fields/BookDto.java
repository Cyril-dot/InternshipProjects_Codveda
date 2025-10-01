package com.LibraryManagementSystem20.LibraryManagementSystemUpgrade.fields;


import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String id;
    private String title;
    private String author;


    // Base64-encoded payloads for sending to client
    private String imageBase64;
    private String imageMime;


    private String pdfBase64;
    private String pdfMime;


    private int publicationYear;
    private int copiesAvailable;
}